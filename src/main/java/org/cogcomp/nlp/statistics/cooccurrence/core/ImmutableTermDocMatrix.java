package org.cogcomp.nlp.statistics.cooccurrence.core;

import edu.illinois.cs.cogcomp.core.io.IOUtils;
import org.cogcomp.nlp.statistics.cooccurrence.lexicon.IncrementalIndexedLexicon;
import org.la4j.matrix.sparse.CCSMatrix;
import org.nustaq.serialization.FSTConfiguration;

import java.io.*;

/**
 * This class uses a sparse matrix in Compressed Column Storage (CCS) format to store term counts for each document
 * This is leveraging the fact that term-document matrices are usually very sparse, even compared to term-term matrices.
 *
 * Use {@Link org.cogcomp.nlp.statistics.cooccurrence.core.TermDocMatrixProcessor TermDocMatrixProcessor}
 * to generate the matrix.
 *
 * @author Sihao Chen
 */

public class ImmutableTermDocMatrix {

    private final CCSMatrix termDocMat;

    // TODO: Can't remember why I did this, but is there a reason why these are not private?
    final int[] colptr;
    final int[] rowidx;
    final double[] val;

    final IncrementalIndexedLexicon lex;
    final IncrementalIndexedLexicon docid;

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
        termDocMat = new CCSMatrix(lex.size(), docid.size(), val.length, val, rowidx, colptr);
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

        this.termDocMat = new CCSMatrix(lex.size(), docid.size(), val.length, val, rowidx, colptr);
    }

    public int getTermTotalCount(String term) {
        return (int) getTermTotalCount(lex.putOrGet(term));
    }

    private double getTermTotalCount(int termID) {
        return termDocMat.getRow(termID).sum();
    }

    public int getCoocurrenceCount(String term1, String term2) {
        return (int) getCoocurrenceCount(lex.putOrGet(term1), lex.putOrGet(term2));
    }

    private double getCoocurrenceCount(int term1, int term2) {
         return termDocMat.getRow(term1).innerProduct(termDocMat.getRow(term2));
    }

    public int getNumTerm() {
        return lexSize;
    }

    public int getNumDoc() {
        return docCount;
    }

    private double getDocwiseTermCount(int termId, int docId) {
        return termDocMat.get(termId, docId);
    }

    public IncrementalIndexedLexicon getLexicon() {
        return this.lex;
    }

    @Override
    public String toString() {
        return this.termDocMat.toString();
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
