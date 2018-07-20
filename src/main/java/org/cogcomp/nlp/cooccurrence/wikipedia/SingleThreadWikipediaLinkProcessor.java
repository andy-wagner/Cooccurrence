package org.cogcomp.nlp.cooccurrence.wikipedia;

import edu.illinois.cs.cogcomp.core.io.IOUtils;
import org.cogcomp.nlp.cooccurrence.core.ImmutableTermDocMatrix;
import org.cogcomp.nlp.cooccurrence.core.SingleThreadTermDocMatrixProcessor;
import org.cogcomp.nlp.cooccurrence.core.TermDocMatrixProcessor;
import org.cogcomp.nlp.cooccurrence.lexicon.IncrementalIndexedLexicon;
import org.cogcomp.nlp.cooccurrence.util.IterableLineReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingleThreadWikipediaLinkProcessor {
    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Usage: java ... [wikipedia-links-path] [out-dir] [save-name]");
            System.exit(1);
        }

        String linksdir = args[0];
        String outdir = args[1];
        String savename = args[2];

        IOUtils.mkdir(outdir);

        Iterable<String> pages = null;
        System.out.print("Reading Links...");
        try {
            pages = new IterableLineReader(linksdir);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Finished!");

        SingleThreadTermDocMatrixProcessor<String> proc = new SingleThreadTermDocMatrixProcessor<String>(pages,
                new IncrementalIndexedLexicon()) {
            @Override
            public List<String> extractTerms(String doc) {
                String[] parts = doc.split("\t");
                if (parts.length != 2)
                    return new ArrayList<>();
                String[] entities = parts[1].split(" ");
                return Arrays.asList(entities);
            }

            @Override
            public String getDocumentId(String doc) {
                String[] parts = doc.split("\t");
                if (parts.length == 2) {
                    return parts[0].split("_")[1];
                } else
                    return null;
            }
        };

        ImmutableTermDocMatrix mat = proc.make();

        try {
            mat.save(outdir, savename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
