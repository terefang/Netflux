package me.fertiz.netflux.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorUtil {

    private static final ExecutorService VIRTUAL_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private static final ExecutorService THREAD_EXECUTOR = Executors.newSingleThreadExecutor();

    private ExecutorUtil() {}

    public static void submitVirtual(Runnable runnable) {
        VIRTUAL_EXECUTOR.submit(runnable);
    }

    public static void submit(Runnable runnable) {
        THREAD_EXECUTOR.submit(runnable);
    }

    public static void shutdown() {
        VIRTUAL_EXECUTOR.shutdown();
        THREAD_EXECUTOR.shutdown();
    }

}
