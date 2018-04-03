package org.cogcomp.nlp.statistics.cooccurrence.core;

import com.google.common.collect.HashBiMap;
import edu.illinois.cs.cogcomp.core.io.LineIO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class TermIDMapping{

    public HashBiMap<String, Integer> termIDMap;

    public TermIDMapping() {
        termIDMap = HashBiMap.create();
    }

    public void put(String term, Integer id) {
        termIDMap.put(term, id);
    }

    public Integer getIdFromTerm(String term) {
        return termIDMap.get(term);
    }

    public String getTermFromId(Integer id) {
        return termIDMap.inverse().get(id);
    }

    /**
     * Load mapping from a CSV file
     * The expected format of the csv file -- N lines, each line contains a String (term) and an integer (id)
     */
    public void readFromCSV(String csvpath, String delimiter) throws FileNotFoundException, IOException{
        List<String> lines = LineIO.read(csvpath);
        for (String line: lines) {
            String[] parsed = line.split(delimiter);
            if (parsed.length != 2)
                continue;

            Integer id = Integer.parseInt(parsed[1]);
            termIDMap.put(parsed[0], id);
        }
    }


}
