package org.cogcomp.nlp.statistics.cooccurrence;

import gnu.trove.list.array.TDoubleArrayList;
import org.cogcomp.nlp.statistics.cooccurrence.core.TermDocumentMatrix;

import java.util.*;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        testDirectInitialization();
    }

//    private static void testInsertSpeed() {
//        long startTime = System.currentTimeMillis();
//
//        Random rand = new Random();
//        TermDocumentMatrix mat = new TermDocumentMatrix(200000, 5000000);
//
//        long endTime = System.currentTimeMillis();
//        double elapsed = (endTime - startTime) / 1000.0D;
//        System.out.println("Initialization Time:\t" + String.format("%.5f", elapsed));
//        startTime = System.currentTimeMillis();
//
//        for (int i = 0; i < 200000; i++) {
//            int term = rand.nextInt(200000);
//            int doc = rand.nextInt(5000000);
//            int count = rand.nextInt(100);
//            mat.addCount(term, doc, count);
//        }
//
//        endTime = System.currentTimeMillis();
//        elapsed = (endTime - startTime) / 1000.0D;
//        System.out.println("Elasped Time:\t" + String.format("%.5f", elapsed));
//    }
//
    private static void testDirectInitialization() {
        double[] value = {1, 5, 7, 2, 3, 6};
        int[] colptr = {0, 1, 3, 4, 5, 6};
        int[] rowidx = {2, 0, 3, 1, 2, 2};

        TermDocumentMatrix mat = new TermDocumentMatrix(4, 5, colptr, rowidx, value);
//        TermDocumentMatrix mat = new TermDocumentMatrix(4, 5);
//
//        mat.addCount(2, 0, 1);
//        mat.addCount(0, 1, 5);
//        mat.addCount(3, 1, 7);
//        mat.addCount(1, 2, 2);
//        mat.addCount(2, 3, 3);
//        mat.addCount(2, 4, 6);
        System.out.println(mat.toString());
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


}
