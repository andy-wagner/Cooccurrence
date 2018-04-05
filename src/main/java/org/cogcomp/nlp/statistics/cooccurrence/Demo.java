package org.cogcomp.nlp.statistics.cooccurrence;

import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import org.cogcomp.nlp.statistics.cooccurrence.core.IIndexedLexicon;
import org.cogcomp.nlp.statistics.cooccurrence.core.IncrementalLinearIndexedLexicon;
import org.cogcomp.nlp.statistics.cooccurrence.core.TermDocumentMatrix;
import org.cogcomp.nlp.statistics.cooccurrence.core.TermDocumentMatrixProcessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Demo {
    public static void main(String[] args) {

        String demoDocsDir = "./data/vanilla/";
        int numThreads = 4;


        List<String> docs = null;
        try {
            String[] _docs = IOUtils.lsFiles(demoDocsDir);
            docs = Arrays.asList(_docs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TermDocumentMatrixProcessor<String> proc = new TermDocumentMatrixProcessor<String>(docs,
                new IncrementalLinearIndexedLexicon(), numThreads) {
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

        TermDocumentMatrix mat = proc.make();
        IIndexedLexicon lex = proc.getLexicon();

        // Get the id of "is" in lexicon
        Integer id = lex.getIdFromTerm("said");

        // result changes every time
        System.out.println(mat.getTermTotalCount(id));
    }
}
