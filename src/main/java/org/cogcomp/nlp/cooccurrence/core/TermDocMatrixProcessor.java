package org.cogcomp.nlp.cooccurrence.core;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import org.cogcomp.nlp.cooccurrence.lexicon.IncrementalIndexedLexicon;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Process a Term-Document Cooccurrence Matrix from a list of "documents" in parallel fashion.
 * Leverage the fact that in Compressed Column Storage (CCS) format sparse matrix, you can
 *
 * @param <T> Type of abstraction that represents the notion of "document"
 */
public abstract class TermDocMatrixProcessor<T> {

    private TIntList rowidx;
    private TIntList colptr;
    private TDoubleList value;

    private ExecutorService exec;

    private Iterable<T> docs;

    private final IncrementalIndexedLexicon term2id;
    private final IncrementalIndexedLexicon doc2id;

    /**
     * Create an instance of TermDocumentProcessor with empty initial lexicon, and assign 4 CPU cores to the processor
     * (or 1/2 total cores available if less than 4).
     *
     * @param docs List of "documents" to be processed
     */
    public TermDocMatrixProcessor(Iterable<T> docs) throws IllegalArgumentException {
        this(docs, new IncrementalIndexedLexicon(), 4);
    }

    /**
     * Create an instance of TermDocumentProcessor, with initial lexicon
     *
     * @param docs List of "documents" to be processed
     * @param term2id initial lexicon. OOV terms seen during the processing will be append to the lexicon.
     * @param threads number of CPU cores (incl HT) you want to assign to this job
     * @throws IllegalArgumentException
     */
    public TermDocMatrixProcessor(Iterable<T> docs, IncrementalIndexedLexicon term2id, int threads) {
        this.rowidx = new TIntArrayList();
        this.colptr = new TIntArrayList();
        this.colptr.add(0);
        this.value = new TDoubleArrayList();
        this.exec = Executors.newFixedThreadPool(threads);
        this.term2id = term2id;
        this.docs = docs;
        this.doc2id = new IncrementalIndexedLexicon();
    }

    /**
     * Call this function to start generating the cooc matrix
     * @return a term-doc cooc matrix
     */
    public ImmutableTermDocMatrix make() {
        Lock lock = new ReentrantLock();
        Collection<Future<?>> futures = new LinkedList<>();
        for (T doc : docs) {
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
        for (int i = 1; i < colptr.size(); i++)
            colptr.set(i, colptr.get(i) + colptr.get(i - 1));

        colptr.toArray();
        return new ImmutableTermDocMatrix(
                colptr.toArray(), rowidx.toArray(), value.toArray(), term2id, doc2id);
    }

    public IncrementalIndexedLexicon getLexicon() {
        return term2id;
    }

    public abstract List<String> extractTerms(T doc);

    public abstract String getDocumentId(T doc);

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

            if (terms.isEmpty())
                return;

            List<Map.Entry<Integer, Long>> sorted = terms.stream()
                    .map(term2id::putOrGet)
                    .collect(Collectors.groupingBy(t -> t, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(Map.Entry<Integer, Long>::getKey))
                    .collect(Collectors.toList());

            for (Map.Entry<Integer, Long> ent: sorted) {
                int termid = ent.getKey();
                double count = ent.getValue().doubleValue();

                _rowidx.add(termid);
                _value.add(count);
            }


            // lock here
            lock.lock();
            int _docidx = -1;
            try {
                String docid = getDocumentId(doc);
                if (docid == null)
                    return;
                else if (doc2id.containsTerm(docid))
                    throw new IllegalArgumentException("Duplicate document ID found: " + docid);
                else {
                    _docidx = doc2id.putOrGet(getDocumentId(doc));
                    rowidx.addAll(_rowidx);
                    colptr.add(sorted.size());
                    value.addAll(_value);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (_docidx % 500 == 0 && _docidx != 0)
                    System.out.println("Processed:\t" + _docidx);
                lock.unlock();
            }
        }
    }
}
