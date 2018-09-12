package com.yingli.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolHelper {
    private static ExecutorService exec = Executors.newFixedThreadPool(50);

    public static void execute(Runnable command) {
        exec.execute(command);
    }

    public static void shutdown() {
        if (exec != null) {
            exec.shutdown();
        }
    }
}
