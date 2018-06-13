package org.cogcomp.nlp.statistics.cooccurrence.lexicon;

import com.google.common.annotations.Beta;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * A synchronized lexicon that stores bidirectional mapping between words and indices
 *  TODO: Design more sophisticated sync patterns
 */
@Beta
public class IndexedLexicon {

    protected final BiMap<String, Integer> lexicon;

    public IndexedLexicon() {
        this.lexicon = HashBiMap.create();
    }

    /**
     * Put a new entry into the lexicon.  If id or word is already present in the lexicon, overwrite the old entry
     * @param word
     * @param id
     */
    public void put(String word, int id) {
        synchronized (lexicon) {
            lexicon.forcePut(word, id);
        }
    }

    public String getWordFromId(int id) {
        return lexicon.inverse().get(id);
    }

    public Integer getIdFromWord(String word) {
        return lexicon.get(word);
    }

    public int size() {
        return lexicon.size();
    }

    public boolean containsWord(String word) {
        if (word == null) {
            return false;
        }
        else {
            return lexicon.containsKey(word);
        }
    }

    public boolean containsId(int id) {
        return lexicon.containsValue(id);
    }
 }
