package org.cogcomp.nlp.cooccurrence.lexicon;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.io.IOUtils;
import org.nustaq.serialization.FSTConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A thread-safe indexed lexicon. Each time a new term is queried, the lexicon would memorize that term and assign an
 * index to it. The index is incremental. For example, if there are currently 8 terms in the lexicon and you add a new
 * term to the lexicon, say "Blueberry Muffin", then the lexicon will assign index 8 to "Blueberry Muffin".
 */
public class IncrementalIndexedLexicon {

    private final BiMap<String, Integer> lexicon;

    private static final FSTConfiguration ser = FSTConfiguration.getDefaultConfiguration();

    public IncrementalIndexedLexicon() {
        lexicon = HashBiMap.create();
    }

    public IncrementalIndexedLexicon(String savePath) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(savePath));
        byte[] bytearr = IOUtils.toByteArray(in);
        this.lexicon = (BiMap<String, Integer>) ser.asObject(bytearr);
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

    public boolean containsTerm(String term) {
        return lexicon.containsKey(term);
    }

    private String getTermFromId(int id) {
        return lexicon.inverse().get(id);
    }

    public int size() {
        return lexicon.size();
    }

    /**
     * Create a list containing all terms currently in lexicon
     * @return a list containing all terms currently in lexicon
     */
    public List<String> getAllWords() {
        List<String> lex = new ArrayList<>();
        for (int i = 0; i < lexicon.size(); i++) {
            lex.add(this.getTermFromId(i));
        }
        return lex;
    }

    public void save(String path) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(path));
        byte[] bytearr = ser.asByteArray(this.lexicon);
        out.write(bytearr);
        out.close();
    }
}
