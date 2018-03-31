package org.cogcomp.nlp.statistics.cooccurrence;

import org.cogcomp.nlp.statistics.cooccurrence.core.TermDocumentMatrix;

import java.util.Random;

public class Test {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        Random rand = new Random();
        TermDocumentMatrix mat = new TermDocumentMatrix(200000, 5000000);

        for (int i = 0; i < 1000000; i++) {
            int term = rand.nextInt(200000);
            int doc = rand.nextInt(5000000);
            int count = rand.nextInt(100);
            mat.addCount(term, doc, count);
        }

        long endTime = System.currentTimeMillis();
        double elapsed = (endTime - startTime) / 1000.0D;
        System.out.println("Elasped Time:\t" + String.format("%.5f", elapsed));


    }
}
