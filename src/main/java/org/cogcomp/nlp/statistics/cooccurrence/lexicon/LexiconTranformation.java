package org.cogcomp.nlp.statistics.cooccurrence.lexicon;

/**
 * Maps entries of {@Link org.cogcomp.nlp.statistics.cooccurrence.lexicon.IncrementalIndexedLexicon
 * IncrementalIndexedLexicon} to new value according to some mapping function
 */
public abstract class LexiconTranformation {
    //TODO
    public abstract String map(String x);
}
