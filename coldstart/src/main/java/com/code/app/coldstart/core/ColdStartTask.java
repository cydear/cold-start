package com.code.app.coldstart.core;

import android.app.Application;
import android.os.Process;
import android.util.Log;

import com.code.app.coldstart.callback.OnTaskFinishListener;
import com.code.app.coldstart.constants.ThreadState;
import com.code.app.coldstart.monitor.OnTaskExceptionMonitor;
import com.code.app.coldstart.monitor.TaskElapsedTimeMonitor;
import com.code.app.coldstart.monitor.TaskExceptionMonitor;
import com.code.app.coldstart.pool.ColdStartThreadPool;
import com.code.app.coldstart.utils.ProcessUtil;
import com.code.app.coldstart.utils.SortUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName ColdStartTask
 * @Author: Lary.huang
 * @CreateDate: 12/25/20 2:25 PM
 * @Version: 1.0
 * @Description: 这个类将一个个关联的{@code ColdStartTask}，组织成PERT网路图的方式进行执行。
 */
public abstract class ColdStartTask implements ITask {
    private Application appContext;
    /**
     * 任务名
     */
    private String methodId;

    /**
     * 线程优先级，线程池是有限的，对于同一时间执行的task，其执行也可能存在先后顺序，值越小，越先执行
     */
    private int priority = Process.THREAD_PRIORITY_DEFAULT;

    /**
     * 实际执行的任务
     */
    private Runnable mTaskRunnable;

    /**
     * 是否运行在主线程
     */
    private boolean isRunOnUiThread;

    /**
     * 任务的执行状态
     */
    private ThreadState mCurrentState = ThreadState.IDLE;

    /**
     * 当前任务的后继任务
     */
    private CopyOnWriteArrayList<ColdStartTask> mSuccessorTasks = new CopyOnWriteArrayList<>();

    /**
     * 当前任务的前驱任务
     */
    private CopyOnWriteArraySet<ColdStartTask> mPredecessorTasks = new CopyOnWriteArraySet<>();

    /**
     * 存储对task的监听
     */
    private List<OnTaskFinishListener> mOnTaskFinishListeners = new ArrayList<>();

    /**
     * 异步任务执行的线程池
     */
    private ThreadPoolExecutor taskExecutor = ColdStartThreadPool.lauchExecutor();

    /**
     * 主线程执行器
     */
    private Executor uiTaskExecutor = ColdStartThreadPool.uiExecutor();

    /**
     * 监控任务执行的异常信息
     */
    private OnTaskExceptionMonitor mExceptionMonitor;
    /**
     * 任务执行耗时监听
     */
    private TaskElapsedTimeMonitor mTaskElapsedTimeMonitor;
    /**
     * 当前任务是否允许在多进程中初始化,默认允许在多进程中初始化
     */
    private boolean enableMutiprocess = false;

    public ColdStartTask(Application appContext, String methodId) {
        this(appContext, methodId, Process.THREAD_PRIORITY_DEFAULT);
    }

    public ColdStartTask(Application appContext, String methodId, int threadPriority) {
        this(appContext, methodId, threadPriority, false);
    }

    public ColdStartTask(Application appContext, String methodId, boolean isRunOnUiThread) {
        this(appContext, methodId, Process.THREAD_PRIORITY_DEFAULT, isRunOnUiThread);
    }

    /**
     * 构造ColdStartTask对象
     *
     * @param methodId        执行的任务名称
     * @param threadPriority  执行任务线程的优先级
     * @param isRunOnUiThread 任务是否需要再主线程中执行
     */
    public ColdStartTask(Application appContext, String methodId, int threadPriority, boolean isRunOnUiThread) {
        this.methodId = methodId;
        this.isRunOnUiThread = isRunOnUiThread;
        this.priority = threadPriority;
        this.appContext = appContext;
    }

    @Override
    public void setEnableMutiprocess(boolean enableMutiprocess) {
        this.enableMutiprocess = enableMutiprocess;
    }

    @Override
    public boolean isEnableMutiprocess() {
        return enableMutiprocess;
    }

    @Override
    public void addOnTaskFinishListener(OnTaskFinishListener listener) {
        if (listener != null) {
            mOnTaskFinishListeners.add(listener);
        }
    }

    public void setTaskExceptionMonitor(OnTaskExceptionMonitor taskExceptionMonitor) {
        this.mExceptionMonitor = taskExceptionMonitor;
    }

    public void setTaskElapsedTimeMonitor(TaskElapsedTimeMonitor taskElapsedTimeMonitor) {
        this.mTaskElapsedTimeMonitor = taskElapsedTimeMonitor;
    }

    @Override
    public void switchThreadState(ThreadState state) {
        this.mCurrentState = state;
    }

    @Override
    public ThreadState getCurrentState() {
        return mCurrentState;
    }

    @Override
    public boolean isRunning() {
        return mCurrentState == ThreadState.RUNNING;
    }

    @Override
    public boolean isFinished() {
        return mCurrentState == ThreadState.FINISHED;
    }

    @Override
    public void setThreadPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getThreadPriority() {
        return this.priority;
    }

    @Override
    public void setThreadPoolExecutor(ThreadPoolExecutor poolExecutor) {
        if (poolExecutor != null) {
            this.taskExecutor = poolExecutor;
        }
    }

    @Override
    public void setUiExecutor(Executor uiExecutor) {
        if (uiExecutor != null) {
            this.uiTaskExecutor = uiExecutor;
        }
    }

    @Override
    public void addPredecessor(ColdStartTask task) {
        if (task != null && !this.mPredecessorTasks.contains(task)) {
            this.mPredecessorTasks.add(task);
        }
    }

    @Override
    public void removePredecessor(ColdStartTask task) {
        if (task != null && this.mPredecessorTasks.contains(task)) {
            this.mPredecessorTasks.remove(task);
        }
    }

    @Override
    public void addSuccessor(ColdStartTask task) {
        if (task == null) {
            Log.e("cold-start", "a task should not null");
            return;
        }
        if (task == this) {
            Log.e("cold-start", "a task should not after itself");
            return;
        }
        //将当前任务设置为task的前置任务
        task.addPredecessor(this);
        //将task加入当前任务的后置任务列表
        mSuccessorTasks.add(task);
    }

    @Override
    public void removeSuccessor(ColdStartTask task) {
        if (task != null && mSuccessorTasks.contains(task)) {
            this.mSuccessorTasks.remove(task);
        }
    }

    @Override
    public synchronized void startTask() {
        if (mCurrentState != ThreadState.IDLE) {
            throw new RuntimeException("you try to run task " + methodId + " twice, is there a circle dependency?");
        }

        Log.d("cold-start", methodId + "=>任务开始执行......");

        switchThreadState(ThreadState.WAIT);

        if (mTaskRunnable == null) {
            mTaskRunnable = () -> {
                Process.setThreadPriority(priority);
                long startTime = System.currentTimeMillis();

                switchThreadState(ThreadState.RUNNING);
                try {
                    ColdStartTask.this.runOnTaskRunnable();
                    reportTaskExceptionMonitor(true, null);
                } catch (Exception e) {
                    reportTaskExceptionMonitor(false, e);
                } finally {
                    switchThreadState(ThreadState.FINISHED);

                    Log.d("cold-start", methodId + "=>任务执行完成......");

                    long endTime = System.currentTimeMillis();
                    recordTime(endTime - startTime);

                    //通知后继任务该前置任务执行完毕
                    notifyTaskFinish();
                    recycle();
                }
            };
        }

        //如果不启用多进程，则任务不执行直接结束
        if (!isEnableMutiprocess() && !ProcessUtil.isMainProcess(appContext)) {
            Log.w("cold-start", methodId + "不能在非主进程中执行，直接跳过=====>name:" + Thread.currentThread().getName());
            notifyTaskFinish();
            recycle();
            return;
        }

        if (isRunOnUiThread) {
            uiTaskExecutor.execute(mTaskRunnable);
        } else {
            taskExecutor.execute(mTaskRunnable);
        }
    }

    @Override
    public void startTaskWithAwait() {

    }

    /**
     * 记录任务执行的耗时
     *
     * @param costTime
     */
    private void recordTime(long costTime) {
        if (mTaskElapsedTimeMonitor != null) {
            mTaskElapsedTimeMonitor.record(methodId, costTime);
        }
    }

    /**
     * 记录任务执行的异常信息
     *
     * @param isSuccess
     * @param e
     */
    private void reportTaskExceptionMonitor(boolean isSuccess, Exception e) {
        if (mExceptionMonitor != null) {
            if (isSuccess) {
                mExceptionMonitor.onTaskSuccessMonitor(methodId);
            } else {
                mExceptionMonitor.onTaskFailMonitor(methodId, e);
            }
        }
    }

    /**
     * 当前任务执行完成，通知后继任务 前置任务已经执行完毕
     */
    @Override
    public void notifyTaskFinish() {
        if (!mSuccessorTasks.isEmpty()) {
            //根据优先级重新排序
            SortUtil.sort(mSuccessorTasks);
            Iterator<ColdStartTask> it = mSuccessorTasks.iterator();
            while (it.hasNext()) {
                ColdStartTask task = it.next();
                //通知所有的后继任务，前置任务已经执行完毕
                if (task != null) {
                    task.onPredecessorFinish(this);
                }
            }
        }
        //回调监听事件
        if (!mOnTaskFinishListeners.isEmpty()) {
            Iterator<OnTaskFinishListener> it = mOnTaskFinishListeners.iterator();
            while (it.hasNext()) {
                OnTaskFinishListener taskListener = it.next();
                if (taskListener != null) {
                    taskListener.onTaskFinish(methodId);
                }
            }
            mOnTaskFinishListeners.clear();
        }
    }

    /**
     * 当前任务的前置任务执行完毕回调此方法
     *
     * @param beforeTask
     */
    @Override
    public void onPredecessorFinish(ColdStartTask beforeTask) {
        if (mPredecessorTasks.isEmpty()) {
            return;
        }
        //从当前的前置任务列表中移除已经执行完毕的前置任务
        mPredecessorTasks.remove(beforeTask);
        //如果前置任务列表已经为空，则表明当前任务可以执行了
        if (mPredecessorTasks.isEmpty()) {
            startTask();
        }
    }

    @Override
    public void recycle() {
        mSuccessorTasks.clear();
        mOnTaskFinishListeners.clear();
    }
}
