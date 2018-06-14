package org.cogcomp.nlp.cooccurrence.util;

import org.slf4j.Logger;

/**
 * Print elapsed running time
 */
public class StopWatch {

    private String jobName;
    private long startTime;

    private Logger logger;
    public StopWatch(String jobName, Logger logger) {
        this.jobName = jobName;
        this.logger = logger;
        start();
    }

    private void start() {
        logger.info(this.jobName + "...");
        this.startTime = System.nanoTime();
    }

    public void finish() {
        long elapsed = System.nanoTime() - this.startTime;
        double elapsedSec = elapsed / 1000000000.0D;
        logger.info("Done [" + elapsedSec + " s]");
    }
}
