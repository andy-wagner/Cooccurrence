import org.cogcomp.nlp.statistics.cooccurrence.lexicon.IncrementalIndexedLexicon;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LexiconTest {

    @Test
    public void testPutOrGet() {
        IncrementalIndexedLexicon lex = new IncrementalIndexedLexicon();

        for (int i = 0; i < 10; i++) {
            lex.putOrGet(Integer.toString(i));
        }

        assertEquals(7, lex.putOrGet("7"));
        assertEquals(3, lex.putOrGet("3"));
    }

    @Test
    public void testSaveLoad() {
        IncrementalIndexedLexicon lex = new IncrementalIndexedLexicon();

        for (int i = 0; i < 10; i++) {
            lex.putOrGet(Integer.toString(i));
        }

        File tmp = null;
        try {
            tmp = File.createTempFile("tmp", "lex");
            lex.save(tmp.getPath());
            IncrementalIndexedLexicon newLex = new IncrementalIndexedLexicon(tmp.getPath());
            assertEquals(newLex.getAllWords(), lex.getAllWords());
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }
}
