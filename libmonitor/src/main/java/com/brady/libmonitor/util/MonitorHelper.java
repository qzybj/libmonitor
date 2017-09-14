package com.brady.libmonitor.util;


import com.brady.libmonitor.model.CpuInfo;
import com.brady.libutil.data.DateUtil;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by zyb
 *
 * @date 2017/9/11
 * @description 监控辅助类
 */
public class MonitorHelper {
    private static final String TAG = MonitorHelper.class.getSimpleName();


    private LinkedList<String> logList ;

    /**
     * 开始监控时间
     */
    private long startWatchTime = 0;
    /**
     * 结束监控时间
     */
    private long endWatchTime = 0;
    /**
     * Break监控时间
     */
    private long breakWatchTime = 0;

    /**
     * 开始监听处理
     */
    public void startWatch(){
        logList = new LinkedList<>();
        startWatchTime = System.currentTimeMillis();
        breakWatchTime = System.currentTimeMillis();
    }

    /**
     * 结束监听处理
     */
    public void endWatch(){
        endWatchTime = System.currentTimeMillis();
        processRecord();
        save2File();
    }

    /**
     * 监听打点，用于区分不同的监听阶段
     * @param tag
     */
    public void setBreakTag(String tag){
        long tmpStamp = System.currentTimeMillis();
        processRecord();
        logList.add(tag+" Break监听耗时"+(tmpStamp - breakWatchTime)+"毫秒");
        breakWatchTime = tmpStamp;
    }

    /**
     * 处理记录逻辑，将监控信息记录下来
     */
    public void processRecord() {
        CpuInfo cpuInfo = PerformanceUtil.getAppCpuUsedPercent();//cpu使用信息

        long availableMemorySize = PerformanceUtil.getAvailableMemorySize();//可用内存大小
        int appMemorySize = PerformanceUtil.getAppMemorySize();//当前app已使用内存
        int cpuRate = PerformanceUtil.getProcessCpuRate();//当前app使用cpu
        String datetime = DateUtil.date2Str(new Date(System.currentTimeMillis()), "yyyyMMdd HH:mm:ss.SSS");//触发时间

        StringBuilder sb = new StringBuilder();
        sb.append("时间" + datetime);
        sb.append(MonitorUtil.PARAMS_SEPARATOR);
        sb.append("app使用内存" + appMemorySize);
        sb.append(MonitorUtil.PARAMS_CUT);
        sb.append("可用内存大小" + availableMemorySize);
        sb.append(MonitorUtil.PARAMS_SEPARATOR);
        if (cpuInfo != null) {
            sb.append("系统程序的CPU占比" + cpuInfo.getSysPercent());
            sb.append(MonitorUtil.PARAMS_CUT);
            sb.append("用户程序的CPU占比" + cpuInfo.getUserPercent());
            sb.append(MonitorUtil.PARAMS_CUT);
            sb.append("当前App的CPU占比" + cpuInfo.getCurAppPercent());
            sb.append(MonitorUtil.PARAMS_CUT);
        }
        sb.append("当前App的CPU使用率" + cpuRate);
        logList.add(sb.toString());
        MonitorUtil.prtLog(TAG, sb.toString());
    }

    private void save2File() {
        MonitorUtil.saveMonitorFile("monitor",logList,startWatchTime,endWatchTime);
    }
}