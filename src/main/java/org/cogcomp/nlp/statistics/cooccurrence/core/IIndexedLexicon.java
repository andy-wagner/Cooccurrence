package org.cogcomp.nlp.statistics.cooccurrence.core;

public interface IIndexedLexicon {
    Integer getIdFromTerm(String term);

    boolean containsTerm(String term);

    String getTermFromId(int id);

    boolean containsID(int id);

    int getIDSize();
}