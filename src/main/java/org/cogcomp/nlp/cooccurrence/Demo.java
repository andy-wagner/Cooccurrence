package org.cogcomp.nlp.cooccurrence;

import org.cogcomp.nlp.cooccurrence.core.CoocMatrixFactory;
import org.cogcomp.nlp.cooccurrence.core.ImmutableTermDocMatrix;

import java.io.IOException;

public class Demo {
    public static void main(String[] args) {
        // This is where I stored the processed counts
        String directory = "/shared/preprocessed/schen149/enwiki-tdmat/curid-tdmat/";
        String matname = "ww-curid";

        // Load the saved counts from the above location into a Term-Document matrix
        // In this case, "Term"s are Wikified links that appear on each Wikipedia page ("Document")
        //
        // Note:    Loading the matrix takes ~20 seconds.
        //          Memory Overhead is 2 or 3GB
        ImmutableTermDocMatrix tdmat = null;
        try {
            tdmat = CoocMatrixFactory.createTermDocMatFromSave(directory, matname);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Retrieve counts for how many times a link appears on a page
        // I used string form of curid to reference everything here
        String obama = "534366";        //curid for Barack_Obama
        String hillary = "5043192";     //curid for Hillary_Clinton
        System.out.print("# of times Barack_Obama appears on Hillary_Clinton's page:\t");
        System.out.println(tdmat.getTermCountInDoc(obama, hillary));
        System.out.print("# of times Hillary_Clinton appears on Barack_Obama's page:\t");
        System.out.println(tdmat.getTermCountInDoc(hillary, obama));

        // Retrieve counts for how many times two links appears together in the entire Wikipedia
        // Note that, if on some page, obama appears 3 times and hillary appears 4 times,
        // it counts as 12 co-occurrence counts instead of just 1. Let me know if you want to try the "1" metric.
        //
        // Let's use obama and hillary as example again
        System.out.print("# of times Barack_Obama and Hillary_Clinton co-occur on the same page:\t");
        System.out.println(tdmat.getCoocCount(obama, hillary));

        // Get the count of a link over the entire Wikipedia
        System.out.print("# of times Barack_Obama appears in the entire Wikipedia:\t");
        System.out.println(tdmat.getTermTotalCount(obama));

        // Given a link, get the ranked list of links that co-occured with the given link with term-term matrix
        // TODO: still need to work on this, because computing term-term matrix is much more costly than computing T-D matrix
    }
}
