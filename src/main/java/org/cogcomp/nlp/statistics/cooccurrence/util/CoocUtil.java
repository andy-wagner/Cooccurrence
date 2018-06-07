package org.cogcomp.nlp.statistics.cooccurrence.util;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CoocUtil {

    /**
     * Bounds the number concurrent executing thread to 1/2 of the cores
     * available to the JVM. If more jobs are submitted than the allowed
     * upperbound, the caller thread will be executing the job.
     *
     * @return a fixed thread pool with bounded job numbers
     */
    public static ThreadPoolExecutor getBoundedThreadPool(int poolSize) {
        poolSize = Math.max(1, poolSize - 1);
        poolSize = Math.min(poolSize, Runtime.getRuntime().availableProcessors() / 2);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                poolSize, // Core count
                poolSize, // Pool Max
                15, TimeUnit.SECONDS, // Thread keep alive time
                new ArrayBlockingQueue<Runnable>(poolSize),// Queue
                new ThreadPoolExecutor.CallerRunsPolicy()// Blocking mechanism
        );
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public static int[] parseIntArray(String arrStr) {
        Scanner scan = new Scanner(arrStr);
        TIntArrayList list = new TIntArrayList();
        while (scan.hasNext())
            list.add(scan.nextInt());

        return list.toArray();
    }

    public static double[] parseDoubleArray(String arrStr) {
        Scanner scan = new Scanner(arrStr);
        TDoubleArrayList list = new TDoubleArrayList();
        while (scan.hasNext())
            list.add(scan.nextDouble());

        return list.toArray();
    }
}
