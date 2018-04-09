package org.cogcomp.nlp.statistics.cooccurrence;

import edu.illinois.cs.cogcomp.annotation.TextAnnotationBuilder;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.nlp.tokenizer.StatefulTokenizer;
import edu.illinois.cs.cogcomp.nlp.utility.TokenizerTextAnnotationBuilder;
import org.cogcomp.nlp.statistics.cooccurrence.core.IncremantalIndexedLexicon;
import org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocumentMatrix;
import org.cogcomp.nlp.statistics.cooccurrence.core.TermDocumentMatrixProcessor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Demo {
    public static void main(String[] args) {

        String demoDocsDir = "./data/vanilla-NYT/";
        int numThreads = 4;


        List<String> docs = null;
        try {
            String[] _docs = IOUtils.lsFiles(demoDocsDir);
            docs = Arrays.asList(_docs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TermDocumentMatrixProcessor<String> proc = new TermDocumentMatrixProcessor<String>(docs,
                new IncremantalIndexedLexicon(), numThreads) {
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

        ImmutableTermDocumentMatrix mat = proc.make();
        IncremantalIndexedLexicon lex = proc.getLexicon();

        // Get the id of "said" in lexicon
        String word = "this";
        int id = lex.putOrGet(word);

        // result changes every time
        System.out.println("ID of " + word + ":\t" + id);
        System.out.println("Count:\t" + mat.getTermTotalCount(id));
        System.out.println("ID count\t" + lex.size());
        System.out.println("Mat entries\t" + mat.getNumTerm());
        System.out.println("Docwise count\t" + mat.getDocwiseTermCount(id));

        // Try to save result!
        try {
            mat.save("out", "vanilla-nyt");
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(mat.toString());
        proc.close();
    }
}
