package com.code.app.coldstart.core;

import com.code.app.coldstart.callback.OnTaskFinishListener;
import com.code.app.coldstart.constants.ThreadState;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName IProject
 * @Author: Lary.huang
 * @CreateDate: 12/26/20 2:32 PM
 * @Version: 1.0
 * @Description: Project对外暴露的接口
 */
public abstract class IProject implements ITask{

    @Override
    public void addOnTaskFinishListener(OnTaskFinishListener listener) {

    }

    @Override
    public void runOnTaskRunnable() {

    }

    @Override
    public ThreadState getCurrentState() {
        return null;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void setThreadPriority(int priority) {

    }

    @Override
    public int getThreadPriority() {
        return 0;
    }

    @Override
    public void setThreadPoolExecutor(ThreadPoolExecutor poolExecutor) {

    }

    @Override
    public void setUiExecutor(Executor uiExecutor) {

    }

    @Override
    public void addPredecessor(ColdStartTask task) {

    }

    @Override
    public void removePredecessor(ColdStartTask task) {

    }

    @Override
    public void addSuccessor(ColdStartTask task) {

    }

    @Override
    public void removeSuccessor(ColdStartTask task) {

    }

    @Override
    public void startTask() {

    }

    @Override
    public void startTaskWithAwait() {

    }

    @Override
    public void switchThreadState(ThreadState state) {

    }

    @Override
    public void notifyTaskFinish() {

    }

    @Override
    public void onPredecessorFinish(ColdStartTask task) {

    }

    @Override
    public void recycle() {

    }

    @Override
    public void setEnableMutiprocess(boolean enableMutiprocess) {

    }

    @Override
    public boolean isEnableMutiprocess() {
        return true;
    }
}
