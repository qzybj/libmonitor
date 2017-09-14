package com.brady.libmonitor.util;

import android.os.Environment;


import com.brady.libmonitor.task.SaveMonitorFileTask;
import com.brady.libutil.data.DateUtil;
import com.brady.libutil.log.CLog;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by zyb
 *
 * @date 2017/9/13
 * @description
 */
public class MonitorUtil {
    private static final String TAG = "CustomMonitor";

    /**参数分隔符*/
    public static final String PARAMS_SEPARATOR ="_";
    /**参数分割符*/
    public static final String PARAMS_CUT ="|";

    /**缓存存储文件夹名称*/
    public static final String CACHE_FOLDER_NAME = "pdamonitor";

    /**
     * 封装日志
     * @param msg
     */
    public static void prtLog(String msg){
        prtLog(TAG,msg);
    }

    /**
     * 封装日志
     * @param tag
     * @param msg
     */
    public static void prtLog(String tag, String msg){
        CLog.d(tag,msg);
    }

    /**存储文件*/
    public static void saveMonitorFile(String tag, LinkedList<String> logList, long startWatchTime, long endWatchTime) {
        String filePath = getSaveFilePath(tag);
        SaveMonitorFileTask task = new SaveMonitorFileTask(filePath,logList,startWatchTime,endWatchTime);
        task.execute("");
    }

    /**
     * 获取文件存储路径
     * @return
     */
    private static String getSaveFilePath(String tag){
        return  getCachePath()+ File.separator +tag+
                DateUtil.date2Str(new Date(System.currentTimeMillis()), "yyyyMMdd HH:mm:ss.SSS");
    }

    /**
     * 获取存储文件夹路径
     * @return
     */
    private static String getCachePath(){
        String dirPath;
        if(Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)){
            dirPath = FileUtil.SDCARD_FOLDER_PATH;
        }else{
            dirPath = FileUtil.APP_CACHE_FOLDER_PATH;
        }
        return dirPath + File.separator +CACHE_FOLDER_NAME;
    }
}