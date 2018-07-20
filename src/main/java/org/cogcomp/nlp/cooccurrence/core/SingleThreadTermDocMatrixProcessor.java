package org.cogcomp.nlp.cooccurrence.core;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;
import org.cogcomp.nlp.cooccurrence.lexicon.IncrementalIndexedLexicon;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Single Threaded Term Doc processor
 *
 * @param <T> Type of abstraction that represents the notion of "document"
 */
public abstract class SingleThreadTermDocMatrixProcessor<T> {

    private TIntList rowidx;
    private TIntList colptr;
    private TDoubleList value;

    private Iterable<T> docs;

    private final IncrementalIndexedLexicon term2id;
    private final IncrementalIndexedLexicon doc2id;

    /**
     * Create an instance of TermDocumentProcessor with empty initial lexicon, and assign 4 CPU cores to the processor
     * (or 1/2 total cores available if less than 4).
     *
     * @param docs List of "documents" to be processed
     */
    public SingleThreadTermDocMatrixProcessor(Iterable<T> docs) throws IllegalArgumentException {
        this(docs, new IncrementalIndexedLexicon());
    }

    /**
     * Create an instance of TermDocumentProcessor, with initial lexicon
     *
     * @param docs List of "documents" to be processed
     * @param term2id initial lexicon. OOV terms seen during the processing will be append to the lexicon.
     * @throws IllegalArgumentException
     */
    public SingleThreadTermDocMatrixProcessor(Iterable<T> docs, IncrementalIndexedLexicon term2id) {
        this.rowidx = new TIntArrayList();
        this.colptr = new TIntArrayList();
        this.colptr.add(0);
        this.value = new TDoubleArrayList();
        this.term2id = term2id;
        this.docs = docs;
        this.doc2id = new IncrementalIndexedLexicon();
    }

    /**
     * Call this function to start generating the cooc matrix
     * @return a term-doc cooc matrix
     */
    public ImmutableTermDocMatrix make() {
        for (T doc: docs) {
            TIntArrayList _rowidx = new TIntArrayList();
            TDoubleArrayList _value = new TDoubleArrayList();

            List<String> terms = extractTerms(doc);

            if (terms.isEmpty())
                continue;

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

            int _docidx = -1;
            String docid = getDocumentId(doc);
            if (docid == null)
                continue;
            else if (doc2id.containsTerm(docid)) {
                System.err.println("Duplicate document ID found: " + docid);
                continue;
            }
            else {
                _docidx = doc2id.putOrGet(getDocumentId(doc));
                rowidx.addAll(_rowidx);
                colptr.add(sorted.size());
                value.addAll(_value);
            }
            if (_docidx % 500 == 0 && _docidx != 0)
                System.out.println("Processed:\t" + _docidx);
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
}
