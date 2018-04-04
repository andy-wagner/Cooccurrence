package org.cogcomp.nlp.statistics.cooccurrence.core;

import edu.illinois.cs.cogcomp.core.io.LineIO;
import org.apache.commons.collections4.set.ListOrderedSet;

import java.io.IOException;
import java.util.List;


public class TermOrderedMap implements ITermIDMapping {

    private ListOrderedSet<String> termIDMap;

    public TermOrderedMap() {
        termIDMap = new ListOrderedSet<>();
    }

    public void put(String term) {
        termIDMap.add(term);
    }

    public Integer getIdFromTerm(String term) {
        return termIDMap.indexOf(term);
    }

    @Override
    public boolean containsTerm(String term) {
        return termIDMap.contains(term);
    }

    @Override
    public boolean containsID(int id) {
        return id < termIDMap.size();
    }

    public String getTermFromId(int id) {
        return termIDMap.get(id);
    }

    public int getIDSize() {
        return termIDMap.size();
    }

    public void reset(){
        termIDMap.clear();
    }
    /**
     * Read terms from file. Each line contains a term, terms id will be created in order
     */
    public void readTermsFromFile(String path) throws IOException{
        reset();
        List<String> lines = LineIO.read(path);
        lines.forEach(this::put);
    }

    /**
     * Read terms from file. Each line contains a term
     */
    public void saveToFile(String path) throws IOException {
        LineIO.write(path, termIDMap);
    }
}
