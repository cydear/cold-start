package com.code.app.coldstart.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcessUtil {
    private static Boolean isMainProcess = null;
    private static String mCurProcessName;

    /**
     * 是否是主进程
     *
     * @return
     */
    public static boolean isMainProcess(Context context) {
        String packageName = context.getPackageName();
        String processName = getProcessName(context);
        return processName == null || processName.equals(packageName);
    }

    /**
     * 是否是主进程
     *
     * @return
     */
    public static boolean isMainProcessChecked(Context context) {
        if (null != isMainProcess) {
            return isMainProcess;
        }
        String packageName = context.getPackageName();
        String processName = getProcessName(context);
        isMainProcess = (processName == null || processName.equals(packageName));
        return isMainProcess;
    }

    public boolean isMainProcesss(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return context.getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }

    //新方法获取进程名
    public static String getProcessName(Context context) {
        if (!TextUtils.isEmpty(mCurProcessName)) {
            return mCurProcessName;
        }
        mCurProcessName = getProcessName(android.os.Process.myPid());
        if (!TextUtils.isEmpty(mCurProcessName)) {
            return mCurProcessName;
        }
        //当第一种方法获取不到进程名称时, 第二种方法获取进程名称
        try {
            int pid = android.os.Process.myPid();
            //获取系统的ActivityManager服务
            android.app.ActivityManager am = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                mCurProcessName = context.getApplicationInfo().packageName;
            }
            for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    mCurProcessName = appProcess.processName;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCurProcessName;
    }

    /**
     * 获取当前进程名
     *
     * @param context ref
     * @return process name
     */
    public static String getCurProcessName(Context context) {
        return getProcessName(context);
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}