package org.cogcomp.nlp.statistics.cooccurrence.core;

import gnu.trove.TCollections;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

public abstract class TermDocumentMatrixLoader {

    private int totalDocs;
    private int totalTerms;

    private int currentDocIndex;

    private TIntList rowidx;
    private TIntList colidx;
    private TDoubleList value;

    public TermDocumentMatrixLoader() {
        rowidx = TCollections.synchronizedList(new TIntArrayList());
        colidx = TCollections.synchronizedList(new TIntArrayList());
        value = TCollections.synchronizedList(new TDoubleArrayList());
        currentDocIndex = 0;
    }

    public void reset() {
        currentDocIndex = 0;
        colidx.clear();
        rowidx.clear();
        value.clear();
    }

    public void run() {

    }

    //    public Set<>


}
