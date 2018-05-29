import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocMatrix;
import org.cogcomp.nlp.statistics.cooccurrence.lexicon.IncrementalIndexedLexicon;
import org.cogcomp.nlp.statistics.cooccurrence.core.TermDocMatrixProcessor;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TermDocMatTest {

    private static final String demoDocsDir = "src/test/resources/vanilla-NYT/";
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

        TermDocMatrixProcessor<String> proc = new TermDocMatrixProcessor<String>(docs,
                new IncrementalIndexedLexicon(), numThreads) {
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
        };

        ImmutableTermDocMatrix mat = proc.make();
        IncrementalIndexedLexicon lex = proc.getLexicon();

        // Get the id of "said" in lexicon
        String word = "this";
        int id = lex.putOrGet(word);

        assertEquals(lex.size(), mat.getNumTerm());
        assertEquals(mat.getTermTotalCount(id), 12);

        proc.close();
    }
}
