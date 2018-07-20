package org.cogcomp.nlp.cooccurrence.core;

import edu.illinois.cs.cogcomp.core.io.IOUtils;
import org.cogcomp.nlp.cooccurrence.lexicon.IncrementalIndexedLexicon;
import org.la4j.matrix.sparse.CCSMatrix;
import org.nustaq.serialization.FSTConfiguration;

import java.io.*;

/**
 * This class uses a sparse matrix in Compressed Column Storage (CCS) format to store term counts for each document
 * This is leveraging the fact that term-document matrices are usually very sparse, even compared to term-term matrices.
 *
 * Use {@Link TermDocMatrixProcessor TermDocMatrixProcessor}
 * to generate the matrix.
 *
 * @author Sihao Chen
 */

public class ImmutableTermDocMatrix {

    final CCSMatrix tdMat;

    private final int[] colptr;
    private final int[] rowidx;
    private final double[] val;

    private final IncrementalIndexedLexicon lex;
    private final IncrementalIndexedLexicon docid;

    private int lexSize;
    private int docCount;

    private static final String LEX_EXT = ".lex";
    private static final String COLPTR_EXT = ".colptr";
    private static final String ROWIDX_EXT = ".rowidx";
    private static final String VAL_EXT = ".val";
    private static final String DOC_EXT = ".doc";

    private static final FSTConfiguration serConfig = FSTConfiguration.getDefaultConfiguration();

    ImmutableTermDocMatrix(int[] colptr, int[] rowidx, double[] val,
                           IncrementalIndexedLexicon lex, IncrementalIndexedLexicon docid) {
        this.colptr = colptr;
        this.rowidx = rowidx;
        this.val = val;
        this.lex = lex;
        this.docid = docid;
        this.lexSize = lex.size();
        this.docCount = docid.size();
        tdMat = new CCSMatrix(lex.size(), docid.size(), val.length, val, rowidx, colptr);
    }

    ImmutableTermDocMatrix(String saveDir, String matName) throws IOException {
        if (!IOUtils.exists(saveDir))
            throw new IOException("Directory not exist! " + saveDir);

        String prefix = saveDir + File.separator + matName;

        FileInputStream colptrIn = new FileInputStream(prefix + COLPTR_EXT);
        this.colptr = (int[]) serConfig.asObject(org.apache.commons.io.IOUtils.toByteArray(colptrIn));
        colptrIn.close();

        FileInputStream rowidxIn = new FileInputStream(prefix + ROWIDX_EXT);
        this.rowidx = (int[]) serConfig.asObject(org.apache.commons.io.IOUtils.toByteArray(rowidxIn));
        rowidxIn.close();

        FileInputStream valIn = new FileInputStream(prefix + VAL_EXT);
        this.val = (double[]) serConfig.asObject(org.apache.commons.io.IOUtils.toByteArray(valIn));
        valIn.close();

        this.lex = new IncrementalIndexedLexicon(prefix + LEX_EXT);
        this.docid = new IncrementalIndexedLexicon(prefix + DOC_EXT);
        this.lexSize = lex.size();
        this.docCount = docid.size();

        this.tdMat = new CCSMatrix(lex.size(), docid.size(), val.length, val, rowidx, colptr);
    }

    /**
     * Get total occurrence counts of a term across all documents
     * @param term
     * @return total occurrence counts of the term across all documents
     */
    public int getTermTotalCount(String term) {
        int id = lex.putOrGet(term);
        if (id >= this.lexSize)
            return 0;
        else
            return (int) _getTermTotalCount(id);
    }

    private double _getTermTotalCount(int termID) {
        return tdMat.getRow(termID).sum();
    }

    /**
     * Get co-occurrence counts of two terms across all documents
     * @param term1
     * @param term2
     * @return co-occurrence counts of term1 and term2
     */
    public int getCoocCount(String term1, String term2) {
        int id1 = lex.putOrGet(term1);
        int id2 = lex.putOrGet(term2);
        if (id1 >= this.lexSize || id2 >= this.lexSize)
            return 0;
        else
            return (int) _getCoocCount(id1, id2);
    }

    private double _getCoocCount(int term1, int term2) {
        return tdMat.getRow(term1).innerProduct(tdMat.getRow(term2));
    }

    /**
     * Get count of a term in a given document
     * @param term
     * @param doc
     * @return count of term in doc
     */
    public int getTermCountInDoc(String term, String doc) {
        int termId = lex.putOrGet(term);
        int docId = docid.putOrGet(doc);
        if (termId >= this.lexSize || docId >= this.docCount)
            return 0;
        else
            return (int)_getDocwiseTermCount(termId, docId);
    }

    private double _getDocwiseTermCount(int termId, int docId) {
        return tdMat.get(termId, docId);
    }

    public int getNumTerm() {
        return lexSize;
    }

    public int getNumDoc() {
        return docCount;
    }

    public IncrementalIndexedLexicon getLexicon() {
        return this.lex;
    }

    @Override
    public String toString() {
        return this.tdMat.toString();
    }

    /**
     * Save the matrix into five separate files, which will have the same file stem but different extensions.
     *
     * - .lex will store the lexicon in linear order
     * - .colptr, .rowidx and .val will store the actual matrix data
     * - .doc will store the document ids in linear order
     *
     * @param dir path of directory to save the matrix
     * @param matName name of the save.
     */
    public void save(String dir, String matName) throws IOException {
        IOUtils.mkdir(dir);
        String prefix = dir + File.separator + matName;

        FileOutputStream colptrOut = new FileOutputStream(prefix + COLPTR_EXT);
        colptrOut.write(serConfig.asByteArray(this.colptr));
        colptrOut.close();

        FileOutputStream rowdixOut = new FileOutputStream(prefix + ROWIDX_EXT);
        rowdixOut.write(serConfig.asByteArray(this.rowidx));
        rowdixOut.close();

        FileOutputStream valOut = new FileOutputStream(prefix + VAL_EXT);
        valOut.write(serConfig.asByteArray(this.val));
        valOut.close();

        lex.save(prefix + LEX_EXT);
        docid.save(prefix + DOC_EXT);
    }
}
