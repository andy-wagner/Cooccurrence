package org.cogcomp.nlp.statistics.cooccurrence.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;

/**
 * A thread-safe indexed lexicon. Each time a new term is queried, the lexicon would memorize that term and assign an
 * index to it.
 *
 */
public class IncremantalIndexedLexicon {

    private final BiMap<String, Integer> termIDMap;

    public IncremantalIndexedLexicon() {
        termIDMap = HashBiMap.create();
    }

    public synchronized int putOrGet(String term) {
        synchronized (termIDMap) {
            if (termIDMap.containsKey(term))
                return termIDMap.get(term);
            else {
                int id = termIDMap.size();
                termIDMap.put(term, id);
                return id;
            }
        }
    }

    public void readFromList(List<String> terms) {
        synchronized (termIDMap) {
            termIDMap.clear();
            terms.forEach(this::putOrGet);
        }
    }

    private String getTermFromId(int id) {
        return termIDMap.inverse().get(id);
    }

    public int size() {
        synchronized (termIDMap) {
            return termIDMap.size();
        }
    }


    public List<String> toList() {
        List<String> lex = new ArrayList<>();
        synchronized (termIDMap) {
            for (int i = 0; i < termIDMap.size(); i++) {
                lex.add(this.getTermFromId(i));
            }
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
