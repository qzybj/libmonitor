package com.brady.libmonitor.task;

import android.os.AsyncTask;


import com.brady.libmonitor.util.MonitorUtil;
import com.brady.libmonitor.util.FileUtil;
import com.brady.libutil.data.ListUtil;
import com.brady.libutil.data.StringUtil;

import java.util.LinkedList;

/**
 * Created by zyb
 *
 * @date 2017/9/13
 * @description 存储监听日志文件
 */
public class SaveMonitorFileTask extends AsyncTask<String, Integer, Boolean> {
    private String mFilePath;
    private LinkedList<String> logList;

    /**
     * 开始监控时间
     */
    private long startWatchTime = 0;
    /**
     * 结束监控时间
     */
    private long endWatchTime = 0;

    public SaveMonitorFileTask(String mFilePath, LinkedList<String> logList, long startWatchTime, long endWatchTime) {
        this.mFilePath = mFilePath;
        this.logList = logList;
        this.startWatchTime = startWatchTime;
        this.endWatchTime = endWatchTime;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if (ListUtil.isNotEmpty(logList)) {
            StringBuilder log = new StringBuilder();
            for (String item : logList) {
                if (StringUtil.isNotEmpty(item)) {
                    log.append(item);
                    log.append(FileUtil.NEW_LINE_SYMBOL);
                }
            }
            if (log.length() > 0) {
                log.append("本次监听耗时" + (endWatchTime - startWatchTime) + "毫秒")
                        .append(FileUtil.NEW_LINE_SYMBOL);
                boolean isSuccess = FileUtil.save2File(mFilePath, log.toString());
                if (isSuccess) {
                    MonitorUtil.prtLog("Save monitor file success!");
                }
                return isSuccess;
            }
        }
        return false;
    }

    protected void onPreExecute() {
        //在 doInBackground(Params...)之前被调用，在ui线程执行
    }

    protected void onCancelled() {
        //在ui线程执行
    }
}
