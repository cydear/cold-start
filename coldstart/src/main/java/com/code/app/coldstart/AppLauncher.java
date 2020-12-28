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

    private static class SingleHolder {
        private static final AppLauncher holder = new AppLauncher();
    }

    public static AppLauncher get() {
        return SingleHolder.holder;
    }

    public AppLauncher builder(Application context) {
        mRootProjectBuilder = new ColdStartProject.Builder(context);
        return AppLauncher.this;
    }

    public AppLauncher add(ColdStartTask task) {
        mRootProjectBuilder.add(task);
        return AppLauncher.this;
    }

    public AppLauncher after(ColdStartTask task) {
        mRootProjectBuilder.after(task);
        return AppLauncher.this;
    }

    public AppLauncher after(ColdStartTask... tasks) {
        mRootProjectBuilder.after(tasks);
        return AppLauncher.this;
    }

    public ColdStartProject createProject() {
        return mRootProjectBuilder.create();
    }
}

