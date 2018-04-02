package org.cogcomp.nlp.statistics.cooccurrence.core;

import java.util.HashMap;

public class TermIDMapping{
    public HashMap<String, Integer> termIDMap;

    public TermIDMapping() {
        termIDMap = new HashMap<>();
    }

    public void put(String term, Integer id) {
        termIDMap.put(term, id);
    }

    public void
}
