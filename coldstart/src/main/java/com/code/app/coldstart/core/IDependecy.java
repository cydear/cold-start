package com.code.app.coldstart.core;

/**
 * @ClassName IDependecy
 * @Author: Lary.huang
 * @CreateDate: 12/28/20 6:01 PM
 * @Version: 1.0
 * @Description: task依赖状态
 */
public interface IDependecy {
    /**
     * 依赖代码执行完，触发解锁
     */
    void dependecyUnlock();

    /**
     * 因为依赖代码未执行完而wait
     * @exception InterruptedException
     */
    void dependecyWait() throws InterruptedException;

    /**
     * 任务是否执行完毕
     *
     * @return
     */
    boolean isDone();
}
