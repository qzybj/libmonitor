package com.brady.libmonitor.util;

import android.os.AsyncTask;
import android.os.Handler;


import com.brady.libutil.data.DateUtil;

import java.util.Date;
import java.util.LinkedList;

/**
 * Created by zyb
 *
 * @date 2017/9/11
 * @description 监控辅助类
 */
public class MonitorPlusHelper {
    private static final String TAG = MonitorHelper.class.getSimpleName();

    public static final int NO_DELAY_SHUTDOWN_TIME = -1;


    private LinkedList<String> logList ;

    /**
     * 触发 - 间隔时间
     */
    public static final int TRIGGER_INTERVAL = 5 * 1000;

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
        startWatchDelayShutDown(NO_DELAY_SHUTDOWN_TIME);
    }

    /**
     *
     * 开始监听处理,并在指定时间后自动结束监听
     * @param delayShutDownTime 毫秒
     */
    public void startWatchDelayShutDown(long delayShutDownTime){
        logList = new LinkedList<>();
        startWatchTime = System.currentTimeMillis();
        breakWatchTime = System.currentTimeMillis();
        startLoop();
        if(delayShutDownTime>0){
            setTimeStopTask(delayShutDownTime);
        }
    }

    /**
     * 结束监听处理
     */
    public void endWatch(){
        endWatchTime = System.currentTimeMillis();
        processRecord();
        save2File();
        stopLoop();
    }

    /**
     * 监听打点，用于区分不同的监听阶段
     * @param tag
     */
    public void setBreakTag(String tag){
        long tmpStamp = System.currentTimeMillis();
        processRecord();
        String msg = tag+" Break监听耗时"+(tmpStamp - breakWatchTime)+"毫秒";
        logList.add(msg);
        MonitorUtil.prtLog(msg);
        breakWatchTime = tmpStamp;
    }

    /**
     * 改为异步任务执行
     */
    private void processRecord() {
        ProcessRecordTask task = new ProcessRecordTask();
        task.execute();
    }

    private void save2File() {
      MonitorUtil.saveMonitorFile("monitor",logList,startWatchTime,endWatchTime);
    }
    /************************************************/
    /********************轮询处理逻辑******************/
    /************************************************/

    /**轮询是否正在运行*/
    private boolean isRunLoop = false;

    private Handler loopHandler;
    /**轮询任务*/
    private LoopTask mLoopTask;
    /**轮询定时关闭任务*/
    private EndTask mEndTask;

    /**轮询执行计数*/
    private int loopCount;

    /**开启轮询*/
    private void startLoop() {
        isRunLoop = true;
        loopHandler = new Handler();
        mLoopTask = new LoopTask();
        loopHandler.post(mLoopTask);
    }

    /**关闭轮询*/
    private void stopLoop() {
        isRunLoop = false;
        if(loopHandler != null){
            try {
                if (mEndTask != null) {
                    loopHandler.removeCallbacks(mEndTask);
                    mEndTask = null;
                }
            } catch (Exception e) {
                MonitorUtil.prtLog(TAG, e.getLocalizedMessage());
            }
            try {
                if (mLoopTask != null) {
                    loopHandler.removeCallbacks(mLoopTask);
                    mLoopTask = null;
                }
            } catch (Exception e) {
                MonitorUtil.prtLog(TAG, e.getLocalizedMessage());
            }
            loopHandler = null;
        }
    }

    /**
     * 轮询任务
     */
    private void loopTask() {
        if (isRunLoop) {
            processRecord();
            loopHandler.postDelayed(mLoopTask, TRIGGER_INTERVAL);
        }
    }

    /**
     * 设置定时关闭任务
     * @param delayShutDownTime
     */
    private void setTimeStopTask(long delayShutDownTime){
        if (delayShutDownTime < 0) {
            delayShutDownTime = 0;
        }
        mEndTask = new EndTask();
        //指定时间后自己关闭
        loopHandler.postDelayed(mEndTask,delayShutDownTime);
    }

    /**
     * 轮询任务
     */
    private class LoopTask implements Runnable {
        @Override
        public void run() {
            if(isRunLoop){
                loopTask();
                loopCount++;
                MonitorUtil.prtLog("loopCount = "+loopCount);
            }else{
                stopLoop();
            }
        }
    }

    /**
     * 定时关闭任务
     */
    private class EndTask implements Runnable {
        @Override
        public void run() {
            endWatch();
        }
    }

    /************************************************/
    /********************轮询任务******************/
    /************************************************/
    public class ProcessRecordTask extends AsyncTask<String, Integer, Boolean> {

        private long currentTime =0;

        public ProcessRecordTask() {
            this.currentTime = System.currentTimeMillis();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            recordLog();
            return true;
        }

        protected void onPreExecute() {}
        protected void onCancelled() {}
        /**
         * 处理记录逻辑，将监控信息记录下来
         */
        private void recordLog() {
            int appMemorySize = PerformanceUtil.getAppMemorySize();//当前app已使用内存
            float cpuRate = CpuUtil.getProcessCpuRate();//当前app使用cpu
            String datetime = DateUtil.date2Str(new Date(System.currentTimeMillis()), "yyyyMMdd HH:mm:ss.SSS");//触发时间

            StringBuilder sb = new StringBuilder();
            sb.append("时间" + datetime);
            sb.append(MonitorUtil.PARAMS_SEPARATOR);
            sb.append("使用内存" + appMemorySize);
            sb.append(MonitorUtil.PARAMS_SEPARATOR);

            sb.append("CPU使用率" + cpuRate);
            logList.add(sb.toString());
            MonitorUtil.prtLog(sb.toString());
        }
//        private void recordLog() {
//            CpuInfo cpuInfo = PerformanceUtil.getAppCpuUsedPercent();//cpu使用信息
//            long availableMemorySize = PerformanceUtil.getAvailableMemorySize();//可用内存大小
//            int appMemorySize = PerformanceUtil.getAppMemorySize();//当前app已使用内存
//            int cpuRate = PerformanceUtil.getProcessCpuRate();//当前app使用cpu
//            String datetime = DateUtil.parse2Date(currentTime, DateUtil.FORMAT_YYYYMMDDHHMMSSSSS);//触发时间
//
//            StringBuilder sb = new StringBuilder();
//            sb.append("时间" + datetime);
//            sb.append(MonitorUtil.PARAMS_SEPARATOR);
//            sb.append("当前app已使用内存" + appMemorySize);
//            sb.append(MonitorUtil.PARAMS_CUT);
//            sb.append("可用内存大小" + availableMemorySize);
//            sb.append(MonitorUtil.PARAMS_SEPARATOR);
//            if (cpuInfo != null) {
//                sb.append("系统程序的CPU占比" + cpuInfo.getSysPercent());
//                sb.append(MonitorUtil.PARAMS_CUT);
//                sb.append("用户程序的CPU占比" + cpuInfo.getUserPercent());
//                sb.append(MonitorUtil.PARAMS_CUT);
//                sb.append("当前App的CPU占比" + cpuInfo.getCurAppPercent());
//                sb.append(MonitorUtil.PARAMS_CUT);
//            }
//            sb.append("当前App的CPU使用率" + cpuRate);
//            logList.add(sb.toString());
//            MonitorUtil.prtLog(sb.toString());
//        }
    }
}