package com.code.app.coldstart.core;

import android.app.Application;

import com.code.app.coldstart.callback.OnColdStartProjectListener;

/**
 * @ClassName ColdStartAnchorTask
 * @Author: Lary.huang
 * @CreateDate: 12/26/20 2:21 PM
 * @Version: 1.0
 * @Description: Project执行的起始和结束节点
 */
public class ColdStartAnchorTask extends ColdStartTask {
    private boolean mIsStartTask;
    private OnColdStartProjectListener onColdStartProjectListener;

    public ColdStartAnchorTask(Application context, String methodId, boolean isStartTask) {
        super(context, methodId);
        this.mIsStartTask = isStartTask;
    }

    public void setOnColdStartProjectListener(OnColdStartProjectListener projectListener) {
        this.onColdStartProjectListener = projectListener;
    }

    @Override
    public void runOnTaskRunnable() {
        if (onColdStartProjectListener != null) {
            if (mIsStartTask) {
                onColdStartProjectListener.onColdStartProjectStart();
            } else {
                onColdStartProjectListener.onColdStartProjectFinish();
            }
        }
    }
}
