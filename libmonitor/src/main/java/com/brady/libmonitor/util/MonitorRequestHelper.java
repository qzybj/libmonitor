package com.brady.libmonitor.util;

import com.brady.libutil.data.DateUtil;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by zyb
 *
 * @date 2017/9/12
 * @description 监控网络请求辅助类
 */
public class MonitorRequestHelper {

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
        MonitorUtil.saveMonitorFile("monitor_request",logList,startWatchTime,endWatchTime);
    }

    /**
     * 监听打点，用于区分不同的监听阶段
     * @param tag
     */
    public void setBreakTag(String tag){
        long tmpStamp = System.currentTimeMillis();
        processRecord();
        String msg = "Break "+tag+" 监听耗时"+(tmpStamp - breakWatchTime)+"毫秒";
        logList.add(msg);
        MonitorUtil.prtLog(MonitorRequestHelper.class.getSimpleName(),msg);
        breakWatchTime = tmpStamp;
    }

    /**
     * 处理记录逻辑，将监控信息记录下来
     */
    private void processRecord() {
        String datetime = "触发时间"+
                DateUtil.date2Str(new Date(System.currentTimeMillis()), "yyyyMMdd HH:mm:ss.SSS");//触发时间
        logList.add("触发时间" + datetime);
        MonitorUtil.prtLog(MonitorRequestHelper.class.getSimpleName(),"触发时间" + datetime);
    }
}