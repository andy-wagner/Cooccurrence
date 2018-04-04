package org.cogcomp.nlp.statistics.cooccurrence.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;


public class LinearIndexedLexicon implements IIndexedLexicon {

    protected BiMap<String, Integer> termIDMap;

    public LinearIndexedLexicon() {
        termIDMap = Maps.synchronizedBiMap(HashBiMap.create());
    }

    public void put(String term) {
        synchronized (termIDMap) {
            int id = termIDMap.size();
            termIDMap.put(term, id);
        }
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

    public List<String> toList() {
        List<String> lex = new ArrayList<>();
        for (int i = 0; i < termIDMap.size(); i++) {
            lex.add(this.getTermFromId(i));
        }

        return lex;
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        List<String> lex = this.toList();
        for (int i = 0; i < lex.size(); i++) {
            str.append(i)
                    .append(":\t")
                    .append(lex.get(i))
                    .append('\n');
        }

        return str.toString();
    }
}
