package com.brady.libmonitor.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Debug;

import com.brady.libmonitor.MonitorManager;
import com.brady.libmonitor.model.CpuInfo;
import com.brady.libutil.data.ListUtil;
import com.brady.libutil.data.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Created by zyb
 *
 * @date 2017/9/11
 * @description 性能监控工具类
 */
public class PerformanceUtil {


    private static Application getContext(){
        return MonitorManager.instance().getApplication();
    }
    /**
     * 杀死应用当前进程
     */
    public static void killProcess() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    /**
     * 获取当前可用内存大小
     * @return
     */
    public static long getAvailableMemorySize(){
        ActivityManager activityManager =
                (ActivityManager)getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        if(info!=null){
            return info.availMem;
        }
        return 0;
    }

    /**
     * 获取当前App的内存
     * @return
     */
    public static int getAppMemorySize(){
        ActivityManager mActivityManager =
                (ActivityManager)getContext().getSystemService(Context.ACTIVITY_SERVICE);
        int pid = getRunningProcessPid();
        if (pid > 0) {
            int[] pidArray = new int[]{pid};
            Debug.MemoryInfo[] memoryInfoArray = mActivityManager.getProcessMemoryInfo(pidArray);
            if( !ListUtil.isEmptyArray(memoryInfoArray) ){
                return memoryInfoArray[0].getTotalPss(); //PSS
            }
        }
        return -1;
    }

    /**
     * 获取当前App的进程ID
     * @return
     */
    public static int getRunningProcessPid(){
        if( getContext() == null ){
            return -1;
        }
        String packageName = getContext().getPackageName();
        if(StringUtil.isEmpty(packageName)){
            return -1;
        }
        ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo ra : run) {
            if (packageName.equals(ra.processName)) {
                return ra.pid;
            }
        }
        return -1;
    }

    /**
     * 根据当前包名获取当前系统/用户/当前App的cpu占比。
     *
     * @return
     */
    public static CpuInfo getAppCpuUsedPercent() {
        String packageName = getContext().getPackageName();//App包名
        if (StringUtil.isEmpty(packageName)) {
            return null;
        }
        BufferedReader reader = null;
        CpuInfo cpuInfo = null;
        try {
            int pid = android.os.Process.myPid();
            reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().
                            exec("top -n 1").getInputStream()), 500);
            String load = reader.readLine();
            int count = 0;
            cpuInfo = new CpuInfo();
            while (load != null) {
                if (load.contains(packageName) && load.contains(String.valueOf(pid))) {
                    count++;
                    String appCPU = processAppCpu(load);
                    if (!StringUtil.isEmpty(appCPU)) {
                        cpuInfo.setCurAppPercent(appCPU);
                    }
                } else if (load.contains("User") && load.contains("System")) {
                    count++;
                    processSysCpu(cpuInfo, load);
                }
                if (count >= 2) {
                    break;
                }
                load = reader.readLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (cpuInfo != null && cpuInfo.isNull()) {
            cpuInfo = null;
        }
        return cpuInfo;
    }

    /**
     * 处理当前app的cpu信息
     *
     * @param result
     */
    private static String processAppCpu(String result) {
        if (StringUtil.isEmpty(result)) {
            return null;
        }
        String[] cpuInfoArray = result.split("%");
        if (cpuInfoArray.length > 0) {
            String[] arrays = cpuInfoArray[0].split(" ");
            if (!ListUtil.isEmptyArray(arrays)) {
                return StringUtil.format(arrays[arrays.length - 1]);
            }
        }
        return null;
    }

    /**
     * 处理系统和用户的CPU情况
     * @param cpuInfo
     * @param result
     */
    private static void processSysCpu(CpuInfo cpuInfo, String result) {
        if (cpuInfo == null ||
                StringUtil.isEmpty(result)) {
            return;
        }
        String[] CpuUsr = result.split("%");
        if( ListUtil.isEmptyArray(CpuUsr)){
            return ;
        }
        String[] CpuUsage = CpuUsr[0].split("User");
        String[] SysUsage = CpuUsr[1].split("System");
        if (CpuUsage.length > 1) {
            cpuInfo.setUserPercent(StringUtil.format(CpuUsage[1]));
        }
        if (SysUsage.length > 1) {
            cpuInfo.setSysPercent(StringUtil.format(SysUsage[1]));
        }
    }

    /** 获取CPU使用率
     * @return
     */
    public static int getProcessCpuRate() {
        StringBuilder sb = new StringBuilder();
        int rate = 0;

        try {
            String Result;
            Process p;
            p = Runtime.getRuntime().exec("top -n 1");

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((Result = br.readLine()) != null) {
                if (Result.trim().length() < 1) {
                    continue;
                } else {
                    String[] CpuUsr = Result.split("%");
                    sb.append("USER:" + CpuUsr[0] + "\n");
                    String[] CpuUsage = CpuUsr[0].split("User");
                    String[] SysUsage = CpuUsr[1].split("System");
                    sb.append("CPU:" + CpuUsage[1].trim() + " length:" + CpuUsage[1].trim().length() + "\n");
                    sb.append("SYS:" + SysUsage[1].trim() + " length:" + SysUsage[1].trim().length() + "\n");
                    rate = Integer.parseInt(CpuUsage[1].trim()) + Integer.parseInt(SysUsage[1].trim());
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rate;
    }
}