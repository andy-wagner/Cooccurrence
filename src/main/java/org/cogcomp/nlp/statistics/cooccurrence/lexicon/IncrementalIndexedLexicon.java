package org.cogcomp.nlp.statistics.cooccurrence.lexicon;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A thread-safe indexed lexicon. E ach time a new term is queried, the lexicon would memorize that term and assign an
 * index to it. The index is incremental. For example, if there are currently 8 terms in the lexicon and you add a new
 * term to the lexicon, say "Blueberry Muffin", then the lexicon will assign index 8 to "Blueberry Muffin".
 */
public class IncrementalIndexedLexicon {

    private final BiMap<String, Integer> lexicon;

    public IncrementalIndexedLexicon() {
        lexicon = HashBiMap.create();
    }

    public int putOrGet(String term) {
        synchronized (lexicon) {
            if (lexicon.containsKey(term))
                return lexicon.get(term);
            else {
                int id = lexicon.size();
                lexicon.put(term, id);
                return id;
            }
        }
    }

    public void readFromList(List<String> terms) {
        synchronized (lexicon) {
            lexicon.clear();
            terms.forEach(this::putOrGet);
        }
    }

    private String getTermFromId(int id) {
        return lexicon.inverse().get(id);
    }

    public int size() {
        synchronized (lexicon) {
            return lexicon.size();
        }
    }

    /**
     * Create a list containing all terms currently in lexicon
     * @return a list containing all terms currently in lexicon
     */
    public List<String> getAllWords() {
        List<String> lex = new ArrayList<>();
        synchronized (lexicon) {
            for (int i = 0; i < lexicon.size(); i++) {
                lex.add(this.getTermFromId(i));
            }
        }
        return lex;
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        List<String> lex = this.getAllWords();
        for (int i = 0; i < lex.size(); i++) {
            str.append(i)
                    .append("\t")
                    .append(lex.get(i))
                    .append('\n');
        }

        return str.toString();
    }
}
