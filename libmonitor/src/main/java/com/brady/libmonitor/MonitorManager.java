package com.brady.libmonitor;

import android.app.Application;

/**
 * Created by zyb
 *
 * @date 2017/9/14
 * @description 监控管理器
 */
public class MonitorManager {

    /**Debug 标记用来控制 是否抛出异常，Crash */
    private boolean isDebug = false;
    private static Application mApplication;
    private static MonitorManager instance = null;

    private MonitorManager() {}

    /**
     * 初始化
     * !!!调用之前必须要初始化
     * @param application
     */
    public static void init(Application application) {
        mApplication = application;
    }

    public synchronized static MonitorManager instance() {
        if (instance==null) {
            instance = new MonitorManager();
        }
        return instance;
    }
    public Application getApplication(){
        return mApplication;
    }

    /**Debug 标记用来控制是否抛出异常，允许Crash */
    public void setDebug(boolean debug) {
        isDebug = debug;
    }

}
