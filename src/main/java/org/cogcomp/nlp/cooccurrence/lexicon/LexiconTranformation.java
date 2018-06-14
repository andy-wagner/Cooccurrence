package org.cogcomp.nlp.cooccurrence.lexicon;

/**
 * Maps entries of {@Link IncrementalIndexedLexicon
 * IncrementalIndexedLexicon} to new value according to some mapping function
 */
public abstract class LexiconTranformation {
    //TODO
    public abstract String map(String x);
}
