package org.cogcomp.nlp.statistics.cooccurrence.core;

import org.ojalgo.function.aggregator.Aggregator;
import org.ojalgo.matrix.store.SparseStore;

/**
 * This is leveraging the fact that term-document matrices are usually very sparse, even compared to term-term matrices.
 * With just a little extra space complexity cost, we can preserve count per document, which is very important
 */
public class TermDocumentMatrix {
    private SparseStore<Double> termDocMat;

    private int numTerm;
    private int numDoc;

    public TermDocumentMatrix(int numTerm, int numDoc) {
        this.numDoc = numDoc;
        this.numTerm = numTerm;
        termDocMat = SparseStore.makePrimitive(numTerm, numDoc);
    }

    public void addCount(int termID, int docID, int count) {
        termDocMat.add(termID, docID, count);
    }

    public double getTermTotalCount(int termID) {
        return termDocMat.aggregateRow(termID, Aggregator.SUM);
    }

    public double getCoocurrenceCount(int term1, int term2) {
        return termDocMat.sliceRow(term1).dot(termDocMat.sliceRow(term2));
    }



}
