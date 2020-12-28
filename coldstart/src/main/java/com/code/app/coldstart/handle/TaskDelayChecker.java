package com.code.app.coldstart.handle;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import com.code.app.coldstart.core.ColdStartProject;

/**
 * @ClassName TaskDelayChecker
 * @Author: Lary.huang
 * @CreateDate: 12/28/20 4:32 PM
 * @Version: 1.0
 * @Description: 任务监视器
 */
public class TaskDelayChecker {
    /**
     * 延时确认任务是否已经完成初始化
     *
     * @param project
     * @param delayTimeMillis
     * @param iTimeOutHandler
     */
    public static void delayCheckTaskAlive(ColdStartProject project, long delayTimeMillis, final ITimeOutHandler iTimeOutHandler) {
        if (delayTimeMillis <= 0 || iTimeOutHandler == null || project == null) {
            return;
        }
        final HandlerThread handlerThread = new HandlerThread("cold-task-timeout-checker", Process.THREAD_PRIORITY_BACKGROUND + Process.THREAD_PRIORITY_MORE_FAVORABLE);
        handlerThread.start();
        new Handler(handlerThread.getLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                //如果当前任务没有执行完成的，输出异常
                if (!project.isFinished()) {
                    //异常兜底处理，结束初始化?
                }
                handlerThread.quit();
            }
        }, delayTimeMillis);
    }
}
