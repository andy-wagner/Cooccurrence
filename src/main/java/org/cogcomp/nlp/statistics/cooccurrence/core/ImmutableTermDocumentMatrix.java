package org.cogcomp.nlp.statistics.cooccurrence.core;

import edu.illinois.cs.cogcomp.core.io.LineIO;
import org.la4j.Vector;
import org.la4j.matrix.sparse.CCSMatrix;
import org.terracotta.modules.ehcache.collections.SerializationHelper;

import java.io.*;
import java.util.Arrays;

/**
 * Using a sparse matrix in Compressed Column Storage (CCS) format to store counts for each document
 * This is leveraging the fact that term-document matrices are usually very sparse, even compared to term-term matrices.
 * With just a little extra space complexity cost (Maybe not, depending on Sparsity)
 * we can preserve count per document, which is very important
 *
 * To optimize (parallel) import speed, I've made this matrix immutable.
 * Use @see org.cogcomp.nlp.statistics.cooccurrence.core.TermDocumentMatrixProcessor to generate the matrix.
 *
 * @author Sihao Chen
 */

public class ImmutableTermDocumentMatrix {

    private final CCSMatrix termDocMat;

    private int numTerm;
    private int numDoc;

    int[] colptr;
    int[] rowidx;
    double[] val;

    protected ImmutableTermDocumentMatrix(int numTerm, int numDoc, int[] colptr, int[] rowidx, double[] val) {
        this.numDoc = numDoc;
        this.numTerm = numTerm;
        this.colptr = colptr;
        this.rowidx = rowidx;
        this.val = val;
        termDocMat = new CCSMatrix(numTerm, numDoc, val.length, val, rowidx, colptr);
    }

    public double getTermTotalCount(int termID) {
        return termDocMat.getRow(termID).sum();
    }

    public double getCoocurrenceCount(int term1, int term2) {
        return termDocMat.getRow(term1).innerProduct(termDocMat.getRow(term2));
    }

    public int getNumTerm() {
        return numTerm;
    }

    public int getNumDoc() {
        return numDoc;
    }

    public Vector getDocwiseTermCount(int termID) {
        return termDocMat.getRow(termID);
    }

    @Override
    public String toString() {
        return this.termDocMat.toString();
    }

    // TODO: make this prettier, I haven't think of a great solution yet -- it's hard do use Generics for primitives in Java
    public void save(OutputStream out) throws IOException {
        StringBuilder str = new StringBuilder();

        for (int cp : colptr) {
            str.append(cp).append(' ');

            if (str.length() > 200000) {
                out.write(str.toString().getBytes());
                str = new StringBuilder();
            }
        }

        str.append('\n');

        for (int ri : rowidx) {
            str.append(ri).append(' ');

            if (str.length() > 200000) {
                out.write(str.toString().getBytes());
                str = new StringBuilder();
            }
        }

        str.append('\n');

        for (double v : val) {
            int _v = (int) v;
            str.append(_v).append(' ');

            if (str.length() > 200000) {
                out.write(str.toString().getBytes());
                str = new StringBuilder();
            }
        }

        out.write(str.toString().getBytes());
    }

}
