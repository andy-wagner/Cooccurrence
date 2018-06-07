import com.google.common.io.Files;
import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import org.cogcomp.nlp.statistics.cooccurrence.core.CoocMatrixFactory;
import org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocMatrix;
import org.cogcomp.nlp.statistics.cooccurrence.lexicon.IncrementalIndexedLexicon;
import org.cogcomp.nlp.statistics.cooccurrence.core.TermDocMatrixProcessor;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TermDocMatTest {

    private static final String demoDocsDir = "src/test/resources/vanilla-NYT/";
    private static final String demoDoc = "src/test/resources/vanilla-NYT/1121146.txt";
    private static final int numThreads = 4;

    @Test
    public void testTDMat() {

        List<String> docs = null;
        try {
            String[] _docs = IOUtils.lsFiles(demoDocsDir);
            docs = Arrays.asList(_docs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TDMatVanillaNYT proc = new TDMatVanillaNYT(docs, new IncrementalIndexedLexicon(), numThreads);

        ImmutableTermDocMatrix mat = proc.make();
        IncrementalIndexedLexicon lex = proc.getLexicon();

        // Get the id of "said" in lexicon
        String word = "this";
        int id = lex.putOrGet(word);

        assertEquals(lex.size(), mat.getNumTerm(), 0);
        assertEquals(mat.getTermTotalCount(id), 12, 0);

        proc.close();
    }

    @Test
    public void testDuplicateFile() {
        List<String> docs = Collections.nCopies(4, demoDoc);

        TDMatVanillaNYT proc = new TDMatVanillaNYT(docs, new IncrementalIndexedLexicon(), numThreads);

        ImmutableTermDocMatrix mat = proc.make();
        IncrementalIndexedLexicon lex = proc.getLexicon();

        String word = "Stalin";
        int id = lex.putOrGet(word);

        assertEquals(mat.getTermTotalCount(id), 2, 0);

        proc.close();
    }

    @Test
    public void testSaveLoad() {
        List<String> docs = null;
        try {
            String[] _docs = IOUtils.lsFiles(demoDocsDir);
            docs = Arrays.asList(_docs);

            TDMatVanillaNYT proc = new TDMatVanillaNYT(docs, new IncrementalIndexedLexicon(), numThreads);
            ImmutableTermDocMatrix mat = proc.make();

            File dir = Files.createTempDir();
            mat.save(dir.getPath(), "vanilla-nyt");

            ImmutableTermDocMatrix newMat = CoocMatrixFactory.createImmutableTermDocMatFromSave(dir.getPath(), "vanilla-nyt");
            IncrementalIndexedLexicon lex = newMat.getLexicon();

            assertEquals(newMat.getTermTotalCount(lex.putOrGet("this")),
                    mat.getTermTotalCount(lex.putOrGet("this")), 0);

            assertEquals(newMat.getNumTerm(), mat.getNumTerm());
            assertEquals(newMat.getNumDoc(), mat.getNumDoc());

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    public class TDMatVanillaNYT extends TermDocMatrixProcessor<String> {
        public TDMatVanillaNYT(List<String> docs, IncrementalIndexedLexicon lex, int threads) {
            super(docs, lex, threads);
        }

        @Override
        public List<String> extractTerms(String doc) {

            String text;
            try {
                text = LineIO.slurp(doc);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }

            TextAnnotationBuilder tab = new TokenizerTextAnnotationBuilder(new StatefulTokenizer());
            TextAnnotation ta = tab.createTextAnnotation(text);

            String[] tokens = ta.getTokens();
            return Arrays.asList(tokens);
        }

        @Override
        public String getDocumentId(String doc) {
            return IOUtils.getFileStem(doc);
        }
    }
}
