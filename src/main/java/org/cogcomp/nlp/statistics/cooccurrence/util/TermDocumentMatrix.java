package org.cogcomp.nlp.statistics.cooccurrence.util;

import org.ojalgo.matrix.store.SparseStore;

/**
 * This is leveraging the fact that term-document matrices are usually very sparse, even compared to term-term matrices.
 * With just a little extra space complexity cost, we can preserve more information about occurrence statistics such as
 * The number of times a term appears in the whole corpora.
 */
public class TermDocumentMatrix {
    SparseStore<>
}
