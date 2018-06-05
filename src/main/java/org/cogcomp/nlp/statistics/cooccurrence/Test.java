package org.cogcomp.nlp.statistics.cooccurrence;

import edu.illinois.cs.cogcomp.thrift.base.Labeling;
import edu.illinois.cs.cogcomp.thrift.curator.Record;
import gnu.trove.list.array.TDoubleArrayList;
import org.apache.commons.io.IOUtils;
import org.cogcomp.nlp.statistics.cooccurrence.core.CooccurrenceMatrixFactory;
import org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocMatrix;
import org.cogcomp.nlp.statistics.cooccurrence.lexicon.IncrementalIndexedLexicon;
import org.cogcomp.nlp.statistics.cooccurrence.util.ProgressReporter;
import org.cogcomp.nlp.statistics.cooccurrence.wikipedia.ExtractWikiEntities;
import org.nustaq.serialization.FSTConfiguration;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        testDeserSpeed();
    }

    private static void testListExpansion() {
        TDoubleArrayList list = new TDoubleArrayList();
        Random rand = new Random();

        long startTime = System.currentTimeMillis();

        TDoubleArrayList buffer = new TDoubleArrayList();
        for (int i = 0; i < 5000000; i++) {
            int numTerms = rand.nextInt(15);
            for (double k = 0; k < numTerms; k++) {
                buffer.add(k);
            }
            list.addAll(buffer);
            buffer.clear();
        }

        long endTime = System.currentTimeMillis();
        double elapsed = (endTime - startTime) / 1000.0D;
        System.out.println("Elasped Time:\t" + String.format("%.5f", elapsed));

    }

    private static void testGroupingBy() {
        List<Integer> l = new ArrayList<>(Arrays.asList(1, 2, 1, 1, 3, 3 ,4, 3));

        Map<Integer, Long> grouped = l.stream()
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

        for (Map.Entry<Integer, Long> ent: grouped.entrySet()) {
            System.out.println(ent.getKey() + " " + ent.getValue().intValue());
        }
    }

    private static void testLoadMat(String matpath, String lexpath) {
//        String matpath = "E:\\work\\corpora\\wikipedia\\links\\title-doc-occ\\enwiki-links.mat";
//        String lexpath = "E:\\work\\corpora\\wikipedia\\links\\title-doc-occ\\enwiki-link.lex";

        try {
            ImmutableTermDocMatrix mat = CooccurrenceMatrixFactory.createImmutableTermDocMatFromSave(lexpath, matpath);
            IncrementalIndexedLexicon lex = mat.getLexicon();
            int id = lex.putOrGet("Barack_Obama");
            System.out.println("ID of obama:\t" + id);
            System.out.println("Total Count Obama:\t" + mat.getTermTotalCount(id));
            System.out.println("Item counts:\t" + mat.getNumTerm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void testScanDouble() {
        String test = "1 2 3 4 5 6";
        Scanner scan = new Scanner(test);
        while (scan.hasNext()) {
            System.out.print(scan.nextInt() + " ");
        }
    }

    private static void testSetToString() {
        HashSet<String> s = new HashSet<>();
        s.add("Nice");
        s.add("Noise");

        System.out.println(s.toString());
    }

    private static void testCuratorRecord() {
        String path = "data/record/wiki2014_1000";
        Record rec = null;
        try {
            byte[] recBytes = IOUtils.toByteArray(new FileInputStream(path));
            rec = ExtractWikiEntities.deserializeRecordFromBytes(recBytes);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Map<String, Labeling> views = rec.getLabelViews();
        for (Map.Entry<String, Labeling> e: views.entrySet()) {
            System.out.println(e);
        }
    }

    private static void testSerializationSpeed() {
        double[] arr = new double[100000000];
        Random rand = new Random();
        ProgressReporter job1 = new ProgressReporter("Populating Array");
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextDouble();
        }
        job1.finish();

        ProgressReporter job2 = new ProgressReporter("Serializing arr to byte[]");
        String outPath = "out/test/";
        FSTConfiguration config = FSTConfiguration.getDefaultConfiguration();
        byte[] bytearr = config.asByteArray(arr);
        job2.finish();

        ProgressReporter job3 = new ProgressReporter("Saving arr to disk");
        try {
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream("out/test/test.txt"));
            bw.write(bytearr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        job3.finish();
    }
    private static void testDeserSpeed() {
        try {
            ProgressReporter job4 = new ProgressReporter("Load arr from disk");
            FSTConfiguration config = FSTConfiguration.getDefaultConfiguration();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream("out/test/test.txt"));
            byte[] byteArr = IOUtils.toByteArray(in);
            job4.finish();

            ProgressReporter job5 = new ProgressReporter("Deserialize arr");
            double[] arr = (double[]) config.asObject(byteArr);
            job5.finish();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
