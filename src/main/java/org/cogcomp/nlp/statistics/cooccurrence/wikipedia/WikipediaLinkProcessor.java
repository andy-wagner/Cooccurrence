package org.cogcomp.nlp.statistics.cooccurrence.wikipedia;

import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocumentMatrix;
import org.cogcomp.nlp.statistics.cooccurrence.core.IncremantalIndexedLexicon;
import org.cogcomp.nlp.statistics.cooccurrence.core.TermDocumentMatrixProcessor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WikipediaLinkProcessor {
    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println("Usage: java [wikipedia-links-path] [out-dir] [save-name] [num-threads]");
            System.exit(1);
        }

        String linksdir = args[0];
        String outdir = args[1];
        String savename = args[2];
        int numThreads = Integer.parseInt(args[3]);

        IOUtils.mkdir(outdir);

        List<String> pages = null;
        try {
            pages = LineIO.read(linksdir);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        TermDocumentMatrixProcessor<String> proc = new TermDocumentMatrixProcessor<String>(pages,
                new IncremantalIndexedLexicon(), numThreads) {
            @Override
            public List<String> extractTerms(String doc) {
                String[] entities = doc.split(" ");
                return Arrays.asList(entities);
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
            mat.save(outdir, savename);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(mat.toString());
        proc.close();
    }
}
