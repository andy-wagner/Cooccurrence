package org.cogcomp.nlp.statistics.cooccurrence.core;

import gnu.trove.TCollections;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import org.cogcomp.nlp.statistics.cooccurrence.util.Util;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class TermDocumentMatrixProcessor<T> {

    private AtomicInteger currentDocIndex;

    private TIntList rowidx;
    private TIntList colidx;
    private TDoubleList value;

    private ThreadPoolExecutor exec;

    private Iterable<T> docs;
    private TermIDMapping term2id;

    public TermDocumentMatrixProcessor(Iterable<T> docs, TermIDMapping term2id, int threads) {
        this.rowidx = TCollections.synchronizedList(new TIntArrayList());
        this.colidx = TCollections.synchronizedList(new TIntArrayList());
        this.value = TCollections.synchronizedList(new TDoubleArrayList());
        this.currentDocIndex = new AtomicInteger(0);
        this.exec = Util.getBoundedThreadPool(threads);
        this.term2id = term2id;
    }

    public void reset() {
        currentDocIndex.set(0);
        colidx.clear();
        rowidx.clear();
        value.clear();
    }

    public void process() {
        for (T doc: docs) {
            exec.execute(new Worker(doc));
        }
    }

    public abstract List<String> extractTerms(T doc);

    private class Worker implements Runnable {

        T doc;
        public Worker(T doc) {
            this.doc = doc;
        }

        public void run() {

            TIntArrayList _rowidx = new TIntArrayList();
            TIntArrayList _colidx = new TIntArrayList();
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

            int docid = currentDocIndex.getAndIncrement();
            rowidx.addAll(_rowidx);
            colidx.addAll()
        }
    }


}
