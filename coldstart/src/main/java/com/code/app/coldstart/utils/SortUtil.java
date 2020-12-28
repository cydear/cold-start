package com.code.app.coldstart.utils;

import com.code.app.coldstart.core.ColdStartTask;

import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @ClassName SortUtil
 * @Author: Lary.huang
 * @CreateDate: 12/25/20 3:59 PM
 * @Version: 1.0
 * @Description: 排序工具类
 */
public class SortUtil {
    /**
     * 根据线程优先级排序
     *
     * @param tasks
     */
    public static void sort(CopyOnWriteArrayList<ColdStartTask> tasks) {
        if (tasks == null || tasks.size() <= 1) {
            return;
        }
        Collections.sort(tasks, (t1, t2) -> t1.getThreadPriority() - t2.getThreadPriority());
    }
}
