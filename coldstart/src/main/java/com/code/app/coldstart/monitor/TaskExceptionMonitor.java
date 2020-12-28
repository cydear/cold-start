package com.code.app.coldstart.monitor;

import android.util.Log;

/**
 * @ClassName TaskExceptionMonitor
 * @Author: Lary.huang
 * @CreateDate: 12/26/20 10:55 PM
 * @Version: 1.0
 * @Description: 任务异常监听通用处理
 */
public class TaskExceptionMonitor implements OnTaskExceptionMonitor {

    @Override
    public void onTaskSuccessMonitor(String methodId) {
        Log.d("cold-start", methodId + "任务执行状态: 正常");
    }

    @Override
    public void onTaskFailMonitor(String methodId, Exception e) {
        Log.e("cold-start", methodId + "任务执行状态: 异常,异常信息:" + e.getMessage());
    }
}
