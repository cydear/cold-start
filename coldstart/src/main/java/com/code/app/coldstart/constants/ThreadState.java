package com.code.app.coldstart.constants;

/**
 * @ClassName ThreadState
 * @Author: Lary.huang
 * @CreateDate: 12/25/20 2:15 PM
 * @Version: 1.0
 * @Description: 线程运行状态
 */
public enum ThreadState {
    /**
     * 空闲状态，线程尚未执行
     */
    IDLE,
    /**
     * 运行状态，线程正在执行
     */
    RUNNING,
    /**
     * 运行结束，线程执行结束
     */
    FINISHED,
    /**
     * 等待，线程等待被执行
     */
    WAIT
}
