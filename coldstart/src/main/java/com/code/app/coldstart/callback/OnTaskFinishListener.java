package com.code.app.coldstart.callback;

/**
 * @ClassName OnTaskFinishListener
 * @Author: Lary.huang
 * @CreateDate: 12/25/20 2:20 PM
 * @Version: 1.0
 * @Description: 任务执行完毕回调
 */
public interface OnTaskFinishListener {
    /**
     * 线程执行完毕回调此方法通知任务执行状态
     *
     * @param taskName
     */
    void onTaskFinish(String taskName);
}
