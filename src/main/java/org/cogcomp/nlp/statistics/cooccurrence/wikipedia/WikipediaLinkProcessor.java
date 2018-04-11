package org.cogcomp.nlp.statistics.cooccurrence.wikipedia;

import edu.illinois.cs.cogcomp.core.io.IOUtils;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocMatrix;
import org.cogcomp.nlp.statistics.cooccurrence.core.IncrementalIndexedLexicon;
import org.cogcomp.nlp.statistics.cooccurrence.core.TermDocMatrixProcessor;

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
        System.out.print("Reading Links...");
        try {
            pages = LineIO.read(linksdir);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Finished!");

        TermDocMatrixProcessor<String> proc = new TermDocMatrixProcessor<String>(pages,
                new IncrementalIndexedLexicon(), numThreads) {
            @Override
            public List<String> extractTerms(String doc) {
                String[] entities = doc.split(" ");
                return Arrays.asList(entities);
            }
        };

        ImmutableTermDocMatrix mat = proc.make();
        IncrementalIndexedLexicon lex = proc.getLexicon();

        // Get the id of "said" in lexicon
        String word = "this";
        int id = lex.putOrGet(word);

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