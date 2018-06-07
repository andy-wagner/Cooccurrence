package org.cogcomp.nlp.statistics.cooccurrence.util;

import org.slf4j.Logger;

public class ProgressReporter {

    private String jobName;
    private long startTime;

    private Logger logger;
    public ProgressReporter(String jobName, Logger logger) {
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
