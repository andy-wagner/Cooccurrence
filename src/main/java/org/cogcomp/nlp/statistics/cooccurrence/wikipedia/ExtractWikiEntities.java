package org.cogcomp.nlp.statistics.cooccurrence.wikipedia;

import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.io.LineIO;
import edu.illinois.cs.cogcomp.thrift.base.Labeling;
import edu.illinois.cs.cogcomp.thrift.base.Span;
import edu.illinois.cs.cogcomp.thrift.curator.Record;
import org.apache.commons.io.IOUtils;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.cogcomp.nlp.statistics.cooccurrence.util.Util;

import java.io.*;
import java.util.List;
import java.util.Map;
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
        private OutputStream out;

        public Processor(String recPath, OutputStream out) {
            this.recPath = recPath;
            this.out = out;
        }

        @Override
        public void run() {
            try {
                byte[] recBytes = IOUtils.toByteArray(new FileInputStream(recPath));
                Record rec = deserializeRecordFromBytes(recBytes);
                Map<String, Labeling> views = rec.getLabelViews();
                if (views.containsKey("wikifier")) {
                    List<Span> spans = views.get("wikifier").getLabels();
                    spans.stream()
                            .map(Span::getLabel)
                            .map(s -> {
                                String[] parts = s.split("/");
                                return parts[parts.length - 1];
                            })
                            .mapToInt()
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Record deserializeRecordFromBytes(byte[] bytes) throws TException {
        TDeserializer deserializer = new TDeserializer(
                new TBinaryProtocol.Factory());
        Record r = new Record();
        deserializer.deserialize(r, bytes);
        return r;
    }


}
