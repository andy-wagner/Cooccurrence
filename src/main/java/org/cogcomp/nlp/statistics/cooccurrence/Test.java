package org.cogcomp.nlp.statistics.cooccurrence;

import gnu.trove.list.array.TDoubleArrayList;
import org.cogcomp.nlp.statistics.cooccurrence.core.ImmutableTermDocumentMatrix;

import java.util.*;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        testListExpansion();
    }

//    private static void testInsertSpeed() {
//        long startTime = System.currentTimeMillis();
//
//        Random rand = new Random();
//        ImmutableTermDocumentMatrix mat = new ImmutableTermDocumentMatrix(200000, 5000000);
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
