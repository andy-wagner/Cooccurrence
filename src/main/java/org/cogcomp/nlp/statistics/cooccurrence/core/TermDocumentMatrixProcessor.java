package org.cogcomp.nlp.statistics.cooccurrence.core;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import org.cogcomp.nlp.statistics.cooccurrence.util.Util;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public abstract class TermDocumentMatrixProcessor<T> {

    private AtomicInteger currentDocIndex;

    private TIntList rowidx;
    private TIntList colidx;
    private TDoubleList value;

    private ThreadPoolExecutor exec;

    private Iterable<T> docs;
    private IIndexedLexicon term2id;

    public TermDocumentMatrixProcessor(Iterable<T> docs, IIndexedLexicon term2id, int threads) {
        this.rowidx = new TIntArrayList();
        this.colidx = new TIntArrayList();
        this.colidx.add(0);
        this.value = new TDoubleArrayList();
        this.currentDocIndex = new AtomicInteger(0);
        this.exec = Util.getBoundedThreadPool(threads);
        this.term2id = term2id;
        this.docs = docs;
    }

    public void reset() {
        currentDocIndex.set(0);
        colidx.clear();
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
        // reduce column indices to column pointers
        for (int i = 1; i < colidx.size(); i++)
            colidx.set(i, colidx.get(i) + colidx.get(i - 1));

        colidx.toArray();
        return new TermDocumentMatrix(term2id.getIDSize(), currentDocIndex.get());
    }

    public IIndexedLexicon getLexicon() {
        return term2id;
    }

    public abstract List<String> extractTerms(T doc);

    public void close() {
        this.exec.shutdown();
    }

    private class Worker implements Runnable {

        T doc;
        Lock lock;

        public Worker(T doc, Lock lock) {
            this.doc = doc;
            this.lock = lock;
        }

        public void run() {

            TIntArrayList _rowidx = new TIntArrayList();
            TDoubleArrayList _value = new TDoubleArrayList();

            List<String> terms = extractTerms(doc);
            Map<Integer, Long> grouped = terms.stream()
                    .map(t -> term2id.getIdFromTerm(t))
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

            for (Map.Entry<Integer, Long> ent: grouped.entrySet()) {
                int termid = ent.getKey();
                double count = ent.getValue().doubleValue();

                _rowidx.add(termid);
                _value.add(count);
            }

            // lock here
            lock.lock();
            try {
                currentDocIndex.getAndIncrement();
                rowidx.addAll(_rowidx);
                colidx.add(grouped.size());
                value.addAll(_value);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                lock.unlock();
                System.out.println("Processed:\t" + doc.toString());
            }
        }
    }


}
