package com.code.app.coldstart.monitor;

/**
 * @ClassName OnTaskExceptionMonitor
 * @Author: Lary.huang
 * @CreateDate: 12/26/20 10:53 PM
 * @Version: 1.0
 * @Description: 任务执行异常监控
 */
public interface OnTaskExceptionMonitor {
    /**
     * 任务执行成功监听
     *
     * @param methodId
     */
    void onTaskSuccessMonitor(String methodId);

    /**
     * 任务执行异常监听
     *
     * @param methodId
     * @param e
     */
    void onTaskFailMonitor(String methodId,Exception e);
}
