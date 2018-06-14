package org.cogcomp.nlp.statistics.cooccurrence;

import edu.illinois.cs.cogcomp.thrift.base.Labeling;
import edu.illinois.cs.cogcomp.thrift.curator.Record;
import gnu.trove.list.array.TDoubleArrayList;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.cogcomp.nlp.statistics.cooccurrence.core.CoocMatrixFactory;
import org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableCoocMatrix;
import org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocMatrix;
import org.cogcomp.nlp.statistics.cooccurrence.lexicon.IncrementalIndexedLexicon;
import org.cogcomp.nlp.statistics.cooccurrence.util.StopWatch;
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
        testTDMatIteration();
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

        StopWatch job1 = new StopWatch("Populating Lexicon", logger);
        IncrementalIndexedLexicon lex = new IncrementalIndexedLexicon();
        for (int i = 0; i < 1000000; i++) {
            lex.putOrGet(Integer.toString(i));
        }
        job1.finish();

        StopWatch job2 = new StopWatch("Saving lexicon to disk", logger);
        try {
            lex.save(outPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        job2.finish();

        StopWatch job3 = new StopWatch("Loading lexicon from disk", logger);
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
        StopWatch job1 = new StopWatch("Populating Array", logger);
        for (int i = 0; i < arr.length; i++) {
            arr[i] = rand.nextDouble();
        }
        job1.finish();

        StopWatch job2 = new StopWatch("Serializing arr to byte[]", logger);
        String outPath = "out/test/";
        FSTConfiguration config = FSTConfiguration.getDefaultConfiguration();
        byte[] bytearr = config.asByteArray(arr);
        job2.finish();

        StopWatch job3 = new StopWatch("Saving arr to disk", logger);
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
            StopWatch job4 = new StopWatch("Load arr from disk", logger);
            FSTConfiguration config = FSTConfiguration.getDefaultConfiguration();
            BufferedInputStream in = new BufferedInputStream(new FileInputStream("out/test/test.txt"));
            byte[] byteArr = IOUtils.toByteArray(in);
            job4.finish();

            StopWatch job5 = new StopWatch("Deserialize arr", logger);
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

    public static void testTDMat() {
        String path = "E:\\wsl-space\\resources\\ww-curid-tdmat";
        String name = "ww-curid";

        StopWatch job1 = new StopWatch("Reading matrix", logger);
        ImmutableTermDocMatrix mat = null;
        try {
            mat = CoocMatrixFactory.createTermDocMatFromSave(path, name);

        } catch (IOException e) {
            e.printStackTrace();
        }
        job1.finish();

        StopWatch job2 = new StopWatch("Getting count", logger);
        double count = mat.getTermTotalCount("1000");
        logger.info("Term Count:\t{}", count);
        job2.finish();

        StopWatch job3 = new StopWatch("Getting cooc count of two terms", logger);
        count = mat.getCoocCount("1000", "14749939");
        logger.info("Cooc Count:\t{}", count);
        job3.finish();

    }

    public static void testTDMatIteration() {
        String path = "E:\\wsl-space\\resources\\ww-curid-tdmat";
        String name = "ww-curid";

        StopWatch job1 = new StopWatch("Reading matrix", logger);
        ImmutableTermDocMatrix mat = null;
        try {
            mat = CoocMatrixFactory.createTermDocMatFromSave(path, name);

        } catch (IOException e) {
            e.printStackTrace();
        }
        job1.finish();

        List<String> words = mat.getLexicon().getAllWords();
        logger.info("Lexicon Size:\t" + words.size());
        StopWatch job2 = new StopWatch("iterating thru 100 elements", logger);
        List<String> someWords = words.subList(0, 100);
        for (String word: someWords) {
            int sum = mat.getTermTotalCount(word);
        }
        job2.finish();
    }
}
