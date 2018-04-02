package org.cogcomp.nlp.statistics.cooccurrence.core;

import com.google.common.collect.HashBiMap;

public class TermIDMapping{
    public HashBiMap<String, Integer> termIDMap;

    public TermIDMapping() {
        termIDMap = HashBiMap.create();
    }

    public void put(String term, Integer id) {
        termIDMap.put(term, id);
    }

    public void getIdFromTerm(String term) {
        termIDMap.get(term);
    }

    public void getTermFromId(Integer id) {
        
    }
}
