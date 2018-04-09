package org.cogcomp.nlp.statistics.cooccurrence.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Util {

    /**
     * Bounds the number concurrent executing thread to 1/2 of the cores
     * available to the JVM. If more jobs are submitted than the allowed
     * upperbound, the caller thread will be executing the job.
     * @return a fixed thread pool with bounded job numbers
     */
    public static ThreadPoolExecutor getBoundedThreadPool(int poolSize) {
        poolSize = Math.max(1, poolSize - 1);
        poolSize = Math.min(poolSize, Runtime.getRuntime().availableProcessors()/ 2);
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


}
