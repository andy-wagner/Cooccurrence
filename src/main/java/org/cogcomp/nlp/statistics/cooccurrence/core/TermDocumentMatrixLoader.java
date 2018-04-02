package org.cogcomp.nlp.statistics.cooccurrence.core;

import gnu.trove.TCollections;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import org.cogcomp.nlp.statistics.cooccurrence.util.Util;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class TermDocumentMatrixLoader {

    private AtomicInteger currentDocIndex;

    private TIntList rowidx;
    private TIntList colidx;
    private TDoubleList value;

    private ThreadPoolExecutor exec;

    public TermDocumentMatrixLoader(int threads) {
        rowidx = TCollections.synchronizedList(new TIntArrayList());
        colidx = TCollections.synchronizedList(new TIntArrayList());
        value = TCollections.synchronizedList(new TDoubleArrayList());
        currentDocIndex = new AtomicInteger(0);
        exec = Util.getBoundedThreadPool(threads);
    }

    public void reset() {
        currentDocIndex.set(0);
        colidx.clear();
        rowidx.clear();
        value.clear();
    }

    public void processNext() {
        exec.execute();
    }


    private class Worker implements Runnable {
        public void run() {

            
            currentDocIndex.incrementAndGet();
        }
    }


}
