package com.code.app.coldstart.core;

import com.code.app.coldstart.callback.OnTaskFinishListener;
import com.code.app.coldstart.constants.ThreadState;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName ITask
 * @Author: Lary.huang
 * @CreateDate: 12/25/20 3:05 PM
 * @Version: 1.0
 * @Description: Task提供的核心接口
 */
public interface ITask {
    /**
     * 为task提供监听
     *
     * @param listener
     */
    void addOnTaskFinishListener(OnTaskFinishListener listener);

    /**
     * 运行执行的任务
     */
    void runOnTaskRunnable();

    /**
     * 当前任务运行的状态
     *
     * @return
     */
    ThreadState getCurrentState();

    /**
     * 当前任务是否正在运行
     *
     * @return
     */
    boolean isRunning();

    /**
     * 当前任务是否执行完毕
     *
     * @return
     */
    boolean isFinished();

    /**
     * 设置执行线程的优先级
     *
     * @param priority
     */
    void setThreadPriority(int priority);

    /**
     * 返回当前任务的执行线程的优先级
     *
     * @return
     */
    int getThreadPriority();

    /**
     * 设置异步执行的线程池
     *
     * @param poolExecutor
     */
    void setThreadPoolExecutor(ThreadPoolExecutor poolExecutor);

    /**
     * 设置主线程执行器
     *
     * @param uiExecutor
     */
    void setUiExecutor(Executor uiExecutor);

    /**
     * 为当前任务指定前置任务
     *
     * @param task
     */
    void addPredecessor(ColdStartTask task);

    /**
     * 移除当前任务指定的前置任务
     *
     * @param task
     */
    void removePredecessor(ColdStartTask task);

    /**
     * 为当前任务添加后置任务
     *
     * @param task
     */
    void addSuccessor(ColdStartTask task);

    /**
     * 移除当前任务的后置任务
     *
     * @param task
     */
    void removeSuccessor(ColdStartTask task);

    /**
     * 任务的执行入口
     */
    void startTask();

    /**
     * 启动任务，会阻塞主线程，带所有task执行完毕后，主线程才会继续执行
     */
    void startTaskWithAwait();

    /**
     * 线程执行状态切换
     *
     * @param state
     */
    void switchThreadState(ThreadState state);

    /**
     * 通知后置任务该前置任务执行完毕
     */
    void notifyTaskFinish();

    /**
     * 前置任务执行完毕回调
     *
     * @param task
     */
    void onPredecessorFinish(ColdStartTask task);

    /**
     * 资源回收
     */
    void recycle();

    /**
     * 设置启动多进程初始化开关
     *
     * @param enableMutiprocess
     */
    void setEnableMutiprocess(boolean enableMutiprocess);

    /**
     * 启动多进程初始化开关
     */
    boolean isEnableMutiprocess();
}
