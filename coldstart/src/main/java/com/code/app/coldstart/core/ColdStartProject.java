package com.code.app.coldstart.core;

import android.app.Application;
import android.text.TextUtils;

import com.code.app.coldstart.callback.OnColdStartProjectListener;
import com.code.app.coldstart.callback.OnTaskFinishListener;
import com.code.app.coldstart.constants.ThreadState;
import com.code.app.coldstart.monitor.OnTaskExceptionMonitor;
import com.code.app.coldstart.monitor.TaskElapsedTimeMonitor;
import com.code.app.coldstart.monitor.TaskExceptionMonitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName ColdStartProject
 * @Author: Lary.huang
 * @CreateDate: 12/26/20 2:10 PM
 * @Version: 1.0
 * @Description: 一个ColdStartProject包含多个Task组成的有序集合，为了完整的描述一个任务序列，Project提供开始任务和结束任务来标识
 * 执行序列的开始和结束。用户添加的任务安插在开始任务之后，结束任务之前.
 */
public class ColdStartProject extends IProject implements IDependecy, OnColdStartProjectListener {
    /**
     * 一个project的唯一标识
     */
    private String methodId;
    /**
     * 起始任务
     */
    private ColdStartAnchorTask startTask;
    /**
     * 结束任务
     */
    private ColdStartAnchorTask endTask;
    /**
     * project 默认methodId
     */
    private static final String DEFAULT_NAME = "ColdStartProject";
    /**
     * 维护Project事件监听
     */
    private List<OnColdStartProjectListener> projectListeners = new ArrayList<>();
    /**
     * 任务执行耗时监控
     */
    private TaskElapsedTimeMonitor mTaskElapsedTimeMonitor;
    /**
     * 任务执行异常监控
     */
    private OnTaskExceptionMonitor mTaskExceptionMonitor;
    /**
     * task被其他模块依赖的条件锁
     */
    private CountDownLatch mBeDependencyLatch;

    public ColdStartProject() {
        this(DEFAULT_NAME);
    }

    public ColdStartProject(String methodId) {
        this.methodId = methodId;
        mTaskElapsedTimeMonitor = new TaskElapsedTimeMonitor();
        mTaskExceptionMonitor = new TaskExceptionMonitor();
        mBeDependencyLatch = new CountDownLatch(1);
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }

    public void setStartTask(ColdStartAnchorTask startTask) {
        this.startTask = startTask;
    }

    public void setEndTask(ColdStartAnchorTask endTask) {
        this.endTask = endTask;
    }

    /**
     * 有向无环图的起始执行点
     */
    @Override
    public void startTask() {
        if (startTask != null) {
            startTask.startTask();
        }
    }

    @Override
    public void startTaskWithAwait() {
        try {
            if (startTask != null) {
                startTask.startTask();
                dependecyWait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ThreadState getCurrentState() {
        if (startTask.getCurrentState() == ThreadState.IDLE) {
            return ThreadState.IDLE;
        } else if (endTask.getCurrentState() == ThreadState.FINISHED) {
            return ThreadState.FINISHED;
        } else {
            return ThreadState.RUNNING;
        }
    }

    @Override
    public boolean isRunning() {
        return getCurrentState() == ThreadState.RUNNING;
    }

    @Override
    public boolean isFinished() {
        return getCurrentState() == ThreadState.FINISHED;
    }

    public void addColdStartProjectListener(OnColdStartProjectListener projectListener) {
        if (projectListener != null) {
            projectListeners.add(projectListener);
        }
    }

    /**
     * Project执行起始任务回调
     */
    @Override
    public void onColdStartProjectStart() {
        if (mTaskElapsedTimeMonitor != null) {
            mTaskElapsedTimeMonitor.recordProjectStartTime();
        }
        Iterator<OnColdStartProjectListener> it = projectListeners.iterator();
        while (it.hasNext()) {
            OnColdStartProjectListener projectListener = it.next();
            if (projectListener != null) {
                projectListener.onColdStartProjectStart();
            }
        }
    }

    /**
     * Project中每一个Task执行结束回调
     */
    @Override
    public void onColdStartTaskFinish() {
        Iterator<OnColdStartProjectListener> it = projectListeners.iterator();
        while (it.hasNext()) {
            OnColdStartProjectListener projectListener = it.next();
            if (projectListener != null) {
                projectListener.onColdStartTaskFinish();
            }
        }
        if (mTaskElapsedTimeMonitor != null) {
            mTaskElapsedTimeMonitor.recordProjectEndTime();
            mTaskElapsedTimeMonitor.record(methodId, mTaskElapsedTimeMonitor.getProjectCostTime());
        }
    }

    /**
     * Project执行结束任务回调
     */
    @Override
    public void onColdStartProjectFinish() {
        Iterator<OnColdStartProjectListener> it = projectListeners.iterator();
        while (it.hasNext()) {
            OnColdStartProjectListener projectListener = it.next();
            if (projectListener != null) {
                projectListener.onColdStartTaskFinish();
            }
        }
        //释放依赖锁
        dependecyUnlock();
    }

    @Override
    public void recycle() {
        super.recycle();
        projectListeners.clear();
    }

    @Override
    public void dependecyUnlock() {
        mBeDependencyLatch.countDown();
    }

    @Override
    public void dependecyWait() throws InterruptedException {
        mBeDependencyLatch.await();
    }

    @Override
    public boolean isDone() {
        return isFinished();
    }

    public static class Builder {
        private Application appContext;
        private ColdStartProject project;
        private ColdStartTask mTempColdStartTask;
        private ColdStartAnchorTask mStartColdTask;
        private ColdStartAnchorTask mEndColdTask;

        public Builder(Application context) {
            this.appContext = context;
            init();
        }

        private void init() {
            mTempColdStartTask = null;
            project = new ColdStartProject();
            mStartColdTask = new ColdStartAnchorTask(appContext, "cold-start-task起始", true);
            mEndColdTask = new ColdStartAnchorTask(appContext, "cold-end-task结束", false);
            mStartColdTask.setOnColdStartProjectListener(project);
            mEndColdTask.setOnColdStartProjectListener(project);
            project.setStartTask(mStartColdTask);
            project.setEndTask(mEndColdTask);
        }

        /**
         * 设置Project执行生命周期的回调
         *
         * @param projectListener
         * @return
         */
        public Builder addColdStartProjectListener(OnColdStartProjectListener projectListener) {
            if (projectListener != null) {
                project.addColdStartProjectListener(projectListener);
            }
            return Builder.this;
        }

        /**
         * 设置Project的MethodId
         *
         * @param methodId
         * @return
         */
        public Builder setProjectMethodId(String methodId) {
            if (!TextUtils.isEmpty(methodId)) {
                project.setMethodId(methodId);
            }
            return Builder.this;
        }

        /**
         * 在初始化有向无环图中增加任务, 如果不显示指定task位置，则Task默认添加在最开始的位置
         *
         * @param task
         */
        private Builder add(ColdStartTask task) {
            if (task == null) {
                throw new IllegalArgumentException("you should add task not null");
            }
            addToRootIfNeed();
            mTempColdStartTask = task;
            mTempColdStartTask.addOnTaskFinishListener(new InnerOnTaskFinishListener(project));
            mTempColdStartTask.setTaskExceptionMonitor(project.mTaskExceptionMonitor);
            mTempColdStartTask.setTaskElapsedTimeMonitor(project.mTaskElapsedTimeMonitor);
            mTempColdStartTask.addSuccessor(mEndColdTask);
            return Builder.this;
        }

        /**
         * 为任务A添加前置任务B，只有前置任务B执行完毕才能执行任务A
         *
         * @param task
         * @return
         */
        private Builder after(ColdStartTask task) {
            if (task == null) {
                throw new IllegalArgumentException("you should add task not null");
            }
            task.addSuccessor(mTempColdStartTask);
            mTempColdStartTask = task;
            mEndColdTask.removePredecessor(task);
            return Builder.this;
        }


        /**
         * 为任务A添加前置任务B，只有前置任务B执行完毕才能执行任务A
         *
         * @param tasks
         * @return
         */
        private Builder after(ColdStartTask... tasks) {
            if (tasks == null) {
                throw new IllegalArgumentException("you should add task not null");
            }
            for (ColdStartTask task : tasks) {
                if (task != null) {
                    after(task);
                }
            }
            return Builder.this;
        }

        /**
         * 通过调用链的形式加入任务执行结构图：A -> B -> C -> D
         *
         * @param tasks
         * @return
         */
        public Builder addTaskChain(ColdStartTask... tasks) {
            if (tasks == null || tasks.length <= 0) {
                throw new IllegalArgumentException("you should add task not null");
            }
            ColdStartTask startTask = tasks[tasks.length - 1];
            add(startTask);
            for (int i = tasks.length - 2; i >= 0; i--) {
                after(tasks[i]);
            }
            return Builder.this;
        }

        private void addToRootIfNeed() {
            if (mTempColdStartTask != null) {
                mStartColdTask.addSuccessor(mTempColdStartTask);
            }
        }

        public ColdStartProject create() {
            addToRootIfNeed();
            ColdStartProject coldStartProject = project;
            //创建完成Project后，重新初始化builder 一遍创建下一个Project
            init();
            return coldStartProject;
        }

        private static class InnerOnTaskFinishListener implements OnTaskFinishListener {
            private ColdStartProject coldStartProject;

            public InnerOnTaskFinishListener(ColdStartProject project) {
                this.coldStartProject = project;
            }

            @Override
            public void onTaskFinish(String taskName) {
                if (coldStartProject != null) {
                    coldStartProject.onColdStartTaskFinish();
                }
            }
        }
    }

}
