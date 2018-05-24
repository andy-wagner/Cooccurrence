package org.cogcomp.nlp.statistics.cooccurrence.wikipedia;

import edu.illinois.cs.cogcomp.core.io.LineIO;
import org.cogcomp.nlp.statistics.cooccurrence.util.Util;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ExtractWikiEntities {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java ExtractWikiEntities [list-of-path-to-records] [out-path] [num-threads]");
            System.exit(1);
        }

        String inList = args[0];
        String outPath = args[1];
        int threads = Integer.parseInt(args[2]);

        ExecutorService pool = Util.getBoundedThreadPool(threads);

        // Read path to Records from a file
        try {
            List<String> paths = LineIO.read(inList);

            for (String path: paths) {
                pool.execute(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    class Processor implements Runnable {

        private String recPath;
        private StringBuilder buffer;

        public Processor(String recPath, StringBuilder buffer) {
            this.recPath = recPath;
            this.buffer = buffer;
        }
        @Override
        public void run() {

        }
    }

}
