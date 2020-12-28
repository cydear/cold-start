package com.code.app.coldstart.callback;

/**
 * @ClassName OnColdStartProjectListener
 * @Author: Lary.huang
 * @CreateDate: 12/25/20 4:37 PM
 * @Version: 1.0
 * @Description: Project执行的生命周期的回调
 */
public interface OnColdStartProjectListener {
    /**
     * 每一组Project执行开始时回调
     */
    void onColdStartProjectStart();

    /**
     * Project中的task执行结束后回调此方法
     */
    void onColdStartTaskFinish();

    /**
     * 当一组Project执行完毕回调Finish方法ß
     */
    void onColdStartProjectFinish();
}
