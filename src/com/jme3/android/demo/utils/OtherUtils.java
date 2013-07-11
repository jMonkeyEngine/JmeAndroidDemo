package com.jme3.android.demo.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author iwgeric
 */
public class OtherUtils {
    private final static Logger logger = Logger.getLogger(OtherUtils.class.getName());
    private static long lastHeapUsed = 0;
    private static long peakHeapUsed = 0;
    private static long numHeapPrints = 0;
    private static long totalHeapUsed = 0;
    private static long avgHeapUsed = 0;

    public static void printMemoryUsed(String tag) {
        long heapMax = Runtime.getRuntime().maxMemory();
        long heapTotal = Runtime.getRuntime().totalMemory();
        long heapFree = Runtime.getRuntime().freeMemory();
        long heapUsed = heapTotal - heapFree;
        long heapIncUsed = heapUsed - lastHeapUsed;

        numHeapPrints++;

        lastHeapUsed = heapUsed;
        if (heapUsed > peakHeapUsed) {
            peakHeapUsed = heapUsed;
        }
        totalHeapUsed += heapUsed;
        avgHeapUsed = totalHeapUsed / numHeapPrints;

        logger.log(Level.INFO, "{0} Heap Stats - Incremental Used: {1}kb, Used: {2}kb, Free: {3}kb, Total: {4}kb, Max: {5}kb, Peak: {6}kb, Avg: {7}kb",
                    new Object[]{
                        tag,
                        (float)(heapIncUsed / 1024),
                        (float)(heapUsed / 1024),
                        (float)(heapFree / 1024),
                        (float)(heapTotal / 1024),
                        (float)(heapMax / 1024),
                        (float)(peakHeapUsed / 1024),
                        (float)(avgHeapUsed / 1024)
                    });

    }

}
