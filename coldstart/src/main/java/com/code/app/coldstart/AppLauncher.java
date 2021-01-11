package com.code.app.coldstart;

import android.app.Application;

import com.code.app.coldstart.core.ColdStartProject;
import com.code.app.coldstart.core.ColdStartTask;

/**
 * @ClassName AppLauncher
 * @Author: Lary.huang
 * @CreateDate: 12/25/20 1:58 PM
 * @Version: 1.0
 * @Description: 冷启动框架入口
 */
public class AppLauncher {
    private ColdStartProject.Builder mRootProjectBuilder;

    private AppLauncher() {

    }

    public static AppLauncher get() {
        return new AppLauncher();
    }

    public AppLauncher builder(Application context) {
        mRootProjectBuilder = new ColdStartProject.Builder(context);
        return AppLauncher.this;
    }

    public AppLauncher addTaskChain(ColdStartTask... tasks) {
        mRootProjectBuilder.addTaskChain(tasks);
        return AppLauncher.this;
    }

    public ColdStartProject createProject() {
        return mRootProjectBuilder.create();
    }
}

