package org.cogcomp.nlp.statistics.cooccurrence.util;

public class ProgressReporter {

    private String jobName;
    private long startTime;

    public ProgressReporter(String jobName) {
        this.jobName = jobName;
        start();
    }

    private void start() {
        System.out.print(this.jobName + "...");
        this.startTime = System.nanoTime();
    }

    public void finish() {
        long elapsed = System.nanoTime() - this.startTime;
        double elapsedSec = elapsed / 1000000000.0D;
        System.out.println("Done [" + elapsedSec + " s]");
    }
}
