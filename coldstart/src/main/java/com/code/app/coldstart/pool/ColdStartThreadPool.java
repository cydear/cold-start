package com.code.app.coldstart.pool;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName ColdStartThreadPool
 * @Author: Lary.huang
 * @CreateDate: 12/25/20 2:01 PM
 * @Version: 1.0
 * @Description: 冷启动线程池
 */
public class ColdStartThreadPool {
    private static ThreadPoolExecutor launchExecutor;
    private static MainThreadExecutor uiExecutor;

    /**
     * 根据cpu个数决定的线程数量
     * 所有线程包括核心线程也会销毁
     * 线程空闲存活时间为5s
     *
     * @return
     */
    public static ThreadPoolExecutor lauchExecutor() {
        if (launchExecutor == null || launchExecutor.isShutdown()) {
            int cpuCount = Runtime.getRuntime().availableProcessors();
            int corePoolSize = cpuCount + 1;
            launchExecutor = new ThreadPoolExecutor(corePoolSize, corePoolSize, 5L, TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(), new ColdStartThreadFactory());
        }
        return launchExecutor;
    }

    public static MainThreadExecutor uiExecutor() {
        if (uiExecutor == null) {
            uiExecutor = new MainThreadExecutor();
        }
        return uiExecutor;
    }

    private static class MainThreadExecutor implements Executor {
        private static Handler mUiHandler;

        MainThreadExecutor() {
            mUiHandler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void execute(Runnable runnable) {
            mUiHandler.post(runnable);
        }
    }

    private static class ColdStartThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadCount = new AtomicInteger(1);
        private final String namePrefix;

        ColdStartThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "cold-start-pool" + poolNumber.getAndIncrement() + "-thread";
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread t = new Thread(group, runnable, namePrefix + threadCount.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            return t;
        }
    }
}
