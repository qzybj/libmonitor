package com.brady.libmonitor.util;

import android.content.Context;
import android.content.Intent;

import com.brady.libmonitor.service.MonitorService;


/**
 * Created by zyb
 *
 * @date 2017/9/12
 * @description
 */
public class MonitorServiceHelper {

    /***
     * 开启监听服务
     * @param context
     */
    public static void startMonitorService(Context context) {
        Intent intent = new Intent(context, MonitorService.class);
        context.startService(intent);
    }

}
