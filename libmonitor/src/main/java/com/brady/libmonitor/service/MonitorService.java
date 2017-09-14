package com.brady.libmonitor.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.brady.libmonitor.util.MonitorHelper;


/**
 * Created by zyb
 *
 * @date 2017/9/12
 * @description 监控Service
 */
public class MonitorService extends IntentService {

    private static boolean isOpen = true;
    /**
     * 触发 - 间隔时间
     */
    public static final int TRIGGER_INTERVAL = 5 * 1000;


    private MonitorHelper monitorHelper;

    public MonitorService(String name) {
        super(name);
    }
    public MonitorService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (isOpen){
            try {
                SystemClock.sleep(TRIGGER_INTERVAL);
                if(monitorHelper==null){
                    monitorHelper = new MonitorHelper();
                    monitorHelper.startWatch();
                }
                monitorHelper.processRecord();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if(monitorHelper!=null){
            monitorHelper.endWatch();
        }
        super.onDestroy();
    }
}