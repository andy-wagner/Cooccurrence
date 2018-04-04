package org.cogcomp.nlp.statistics.cooccurrence.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;


public class LinearIndexedLexicon implements IIndexedLexicon {

    protected BiMap<String, Integer> termIDMap;

    public LinearIndexedLexicon() {
        termIDMap = HashBiMap.create();
        termIDMap = Maps.synchronizedBiMap(termIDMap);
    }

    public synchronized void put(String term) {
        int id = termIDMap.size();
        termIDMap.put(term, id);
    }

    public Integer getIdFromTerm(String term) {
        return termIDMap.get(term);
    }

    @Override
    public boolean containsTerm(String term) {
        return termIDMap.containsKey(term);
    }

    @Override
    public boolean containsID(int id) {
        return id < termIDMap.size();
    }

    public String getTermFromId(int id) {
        return termIDMap.inverse().get(id);
    }

    public int getIDSize() {
        return termIDMap.size();
    }

    public void reset(){
        termIDMap.clear();
    }

    public void readFromList(List<String> terms) {
        reset();
        terms.forEach(this::put);
    }

    public List<String> saveToList() {
        List<String> lex = new ArrayList<>();
        for (int i = 0; i < termIDMap.size(); i++) {
            lex.add(this.getTermFromId(i));
        }

        return lex;
    }
}
