package org.cogcomp.nlp.statistics.cooccurrence;

import edu.illinois.cs.cogcomp.thrift.base.Labeling;
import edu.illinois.cs.cogcomp.thrift.curator.Record;
import gnu.trove.list.array.TDoubleArrayList;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.cogcomp.nlp.statistics.cooccurrence.core.CoocMatrixFactory;
import org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocMatrix;
import org.cogcomp.nlp.statistics.cooccurrence.lexicon.IncrementalIndexedLexicon;
import org.cogcomp.nlp.statistics.cooccurrence.util.ProgressReporter;
import org.cogcomp.nlp.statistics.cooccurrence.wikipedia.ExtractWikiEntities;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Test {

    private static Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        testCuratorRecord();
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
            ImmutableTermDocMatrix mat = CoocMatrixFactory.createImmutableTermDocMatFromSave(lexpath, matpath);
            IncrementalIndexedLexicon lex = mat.getLexicon();
            int id = lex.putOrGet("Barack_Obama");
            System.out.println("ID of obama:\t" + id);
            System.out.println("Total Count Obama:\t" + mat.getTermTotalCount(id));
            System.out.println("Item counts:\t" + mat.getNumTerm());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static void testLexiconSaveLoad() {
        String outPath = "out/test/test.lex";

        ProgressReporter job1 = new ProgressReporter("Populating Lexicon", logger);
        IncrementalIndexedLexicon lex = new IncrementalIndexedLexicon();
        for (int i = 0; i < 1000000; i++) {
            lex.putOrGet(Integer.toString(i));
        }
        job1.finish();

        ProgressReporter job2 = new ProgressReporter("Saving lexicon to disk", logger);
        try {
            lex.save(outPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        job2.finish();

        ProgressReporter job3 = new ProgressReporter("Loading lexicon from disk", logger);
        try {
            lex = new IncrementalIndexedLexicon(outPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        job3.finish();
    }

    private static void testSerializationSpeed() {
        double[] arr = new double[100000000];
        Random rand = new Random();
        ProgressReporter job1 = new ProgressReporter("Populating Array", logger);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextDouble();
        }
        job1.finish();

        ProgressReporter job2 = new ProgressReporter("Serializing arr to byte[]", logger);
        String outPath = "out/test/";
        FSTConfiguration config = FSTConfiguration.getDefaultConfiguration();
        byte[] bytearr = config.asByteArray(arr);
        job2.finish();

        ProgressReporter job3 = new ProgressReporter("Saving arr to disk", logger);
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
            ProgressReporter job4 = new ProgressReporter("Load arr from disk", logger);
            FSTConfiguration config = FSTConfiguration.getDefaultConfiguration();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream("out/test/test.txt"));
            byte[] byteArr = IOUtils.toByteArray(in);
            job4.finish();

            ProgressReporter job5 = new ProgressReporter("Deserialize arr", logger);
            double[] arr = (double[]) config.asObject(byteArr);
            job5.finish();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void testGetFileStem() {
        String path = "out/test/vanilla-nyt.colptr";
        System.out.println(FilenameUtils.getBaseName(path));
    }
}
