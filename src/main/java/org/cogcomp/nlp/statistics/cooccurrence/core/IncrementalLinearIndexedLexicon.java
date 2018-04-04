package org.cogcomp.nlp.statistics.cooccurrence.core;

/**
 * Bascially same structure as @see org.cogcomp.nlp.statistics.cooccurrence.core.LinearIndexedLexicon
 * The only difference is it updates the lexicon every time you query for a OOV term.
 *
 * @author Sihao
 */
public class IncrementalLinearIndexedLexicon extends LinearIndexedLexicon {

    public IncrementalLinearIndexedLexicon() {
        super();
    }

    @Override
    public Integer getIdFromTerm(String term) {
        if (!this.containsTerm(term)) {
            this.put(term);
        }
        return this.termIDMap.get(term);
    }
}
