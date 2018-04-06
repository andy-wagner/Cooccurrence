package org.cogcomp.nlp.statistics.cooccurrence.core;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import org.cogcomp.nlp.statistics.cooccurrence.util.Util;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public abstract class TermDocumentMatrixProcessor<T> {

    private AtomicInteger currentDocIndex;

    private TIntList rowidx;
    private TIntList colptr;
    private TDoubleList value;

    private ExecutorService exec;

    private Iterable<T> docs;
    private final IncremantalIndexedLexicon term2id;

    public TermDocumentMatrixProcessor(Iterable<T> docs, IncremantalIndexedLexicon term2id, int threads) {
        this.rowidx = new TIntArrayList();
        this.colptr = new TIntArrayList();
        this.colptr.add(0);
        this.value = new TDoubleArrayList();
        this.currentDocIndex = new AtomicInteger(0);
        this.exec = Executors.newFixedThreadPool(threads);
        this.term2id = term2id;
        this.docs = docs;
        term2id.putOrGet("said"); //debug
    }

    public void reset() {
        currentDocIndex.set(0);
        colptr.clear();
        rowidx.clear();
        rowidx.add(0);
        value.clear();
    }

    public TermDocumentMatrix make() {
        Lock lock = new ReentrantLock();
        Collection<Future<?>> futures = new LinkedList<>();
        for (T doc: docs) {
            futures.add(exec.submit(new Worker(doc, lock)));
        }

        for (Future<?> ft: futures) {
            try {
                ft.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        try {
//            exec.awaitTermination(5, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // reduce column indices to column pointers
        for (int i = 1; i < colptr.size(); i++)
            colptr.set(i, colptr.get(i) + colptr.get(i - 1));

        colptr.toArray();
        return new TermDocumentMatrix(term2id.size(), currentDocIndex.get(),
                colptr.toArray(), rowidx.toArray(), value.toArray());
    }

    public IncremantalIndexedLexicon getLexicon() {
        return term2id;
    }

    public abstract List<String> extractTerms(T doc);

    public void close() {
        this.exec.shutdown();
    }

    private class Worker implements Runnable {

        T doc;
        final Lock lock;

        public Worker(T doc, Lock lock) {
            this.doc = doc;
            this.lock = lock;
        }

        public void run() {

            TIntArrayList _rowidx = new TIntArrayList();
            TDoubleArrayList _value = new TDoubleArrayList();

            List<String> terms = extractTerms(doc);

            Map<Integer, Long> grouped = terms.stream()
                    .map(term2id::putOrGet)
                    .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

            for (Map.Entry<Integer, Long> ent: grouped.entrySet()) {
                int termid = ent.getKey();
                double count = ent.getValue().doubleValue();

                if (termid == 0)
                    System.out.println(count); // debug

                _rowidx.add(termid);
                _value.add(count);
            }

            // lock here
            synchronized (lock) {
                try {
                    currentDocIndex.getAndIncrement();
                    rowidx.addAll(_rowidx);
                    colptr.add(grouped.size());
                    value.addAll(_value);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
//                lock.unlock();
                    System.out.println("Processed:\t" + doc.toString());
                    System.out.println(grouped.toString());
                }
            }

        }
    }


}
