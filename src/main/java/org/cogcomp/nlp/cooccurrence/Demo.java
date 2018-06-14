package org.cogcomp.nlp.cooccurrence;

import org.cogcomp.nlp.cooccurrence.core.CoocMatrixFactory;
import org.cogcomp.nlp.cooccurrence.core.ImmutableTermDocMatrix;
import org.cogcomp.nlp.cooccurrence.util.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Demo {

    private static Logger logger = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {
        // This is where I stored the processed counts
        String directory = "/shared/preprocessed/schen149/enwiki-tdmat/curid-tdmat/";
        String matname = "ww-curid";

        // Load the saved counts from the above location into a Term-Document matrix
        // In this case, "Term"s are Wikified links that appear on each Wikipedia page ("Document")
        //
        // Note:    Loading the matrix takes ~100 seconds overhead on cogcomp server
        //          On my own laptop it takes about 10 seconds.
        //          Memory usage around 2 or 3GB

        StopWatch timer = new StopWatch("Loading Matrix from save", logger);
        ImmutableTermDocMatrix tdmat = null;
        try {
            tdmat = CoocMatrixFactory.createTermDocMatFromSave(directory, matname);
        } catch (IOException e) {
            e.printStackTrace();
        }
        timer.finish();

        // Retrieve counts for how many times a link appears on a page
        // I used string form of curid to reference everything here
        String obama = "534366";        //curid for Barack_Obama
        String hillary = "5043192";     //curid for Hillary_Clinton

        int count1 = tdmat.getTermCountInDoc(obama, hillary);
        logger.info("# of times Barack_Obama appears on Hillary_Clinton's page:\t{}", count1);

        int count2 = tdmat.getTermCountInDoc(hillary, obama);
        logger.info("# of times Hillary_Clinton appears on Barack_Obama's page:\t{}", count2);

        // Retrieve counts for how many times two links appears together in the entire Wikipedia
        // Note that, if on some page, obama appears 3 times and hillary appears 4 times,
        // it counts as 12 co-occurrence counts instead of just 1. Let me know if you want to try the "1" metric.
        //
        // Let's use obama and hillary as example again
        int coocCount = tdmat.getCoocCount(obama, hillary);
        logger.info("# of times Barack_Obama and Hillary_Clinton co-occur on the same page:\t{}", coocCount);

        // Get the count of a link over the entire Wikipedia
        int totalCount = tdmat.getTermTotalCount(obama);
        logger.info("# of times Barack_Obama appears in the entire Wikipedia:\t{}", totalCount);

        // Given a link, get the ranked list of links that co-occured with the given link with term-term matrix
        // TODO: still need to work on this, because computing term-term matrix is much more costly than computing T-D matrix
    }
}
