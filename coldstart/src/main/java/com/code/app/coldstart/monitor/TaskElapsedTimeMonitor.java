package com.code.app.coldstart.monitor;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName TaskExecuteMonitor
 * @Author: Lary.huang
 * @CreateDate: 12/26/20 11:15 PM
 * @Version: 1.0
 * @Description: 任务耗时监控，记录每个任务执行的耗时时间
 */
public class TaskElapsedTimeMonitor {
    private Map<String, Long> mExecuteTimeMap = new HashMap<>();
    private long mProjectStartTime;
    private long mProjectEndTime;

    /**
     * 记录任务执行的时间
     *
     * @param methodId
     * @param costTime
     */
    public synchronized void record(String methodId, long costTime) {
        Log.d("cold-start", methodId + "耗时:" + costTime + ", 执行线程:" + Thread.currentThread().getName());
        if (!mExecuteTimeMap.containsKey(methodId)) {
            mExecuteTimeMap.put(methodId, costTime);
        }
    }

    public Map<String, Long> getExecuteTimeMap() {
        return mExecuteTimeMap;
    }

    public void recordProjectStartTime() {
        this.mProjectStartTime = System.currentTimeMillis();
    }

    public void recordProjectEndTime() {
        this.mProjectEndTime = System.currentTimeMillis();
    }

    public long getProjectCostTime() {
        return mProjectEndTime - mProjectStartTime;
    }
}
