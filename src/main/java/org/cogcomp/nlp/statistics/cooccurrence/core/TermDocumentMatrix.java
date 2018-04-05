package org.cogcomp.nlp.statistics.cooccurrence.core;

import org.la4j.Vector;
import org.la4j.matrix.sparse.CCSMatrix;

/**
 * Using a sparse matrix in Compressed Column Storage (CCS) format to store counts for each document
 * This is leveraging the fact that term-document matrices are usually very sparse, even compared to term-term matrices.
 * With just a little extra space complexity cost (Maybe not, depending on Sparsity)
 * we can preserve count per document, which is very important
 *
 * @author Sihao Chen
 */

public class TermDocumentMatrix {
    private final CCSMatrix termDocMat;

    private int numTerm;
    private int numDoc;

    public TermDocumentMatrix(int numTerm, int numDoc) {
        this.numDoc = numDoc;
        this.numTerm = numTerm;
        termDocMat = new CCSMatrix(numTerm, numDoc);
    }

    public TermDocumentMatrix(int numTerm, int numDoc, int[] colptr, int[] rowidx, double[] val) {
        this.numDoc = numDoc;
        this.numTerm = numTerm;
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
}
