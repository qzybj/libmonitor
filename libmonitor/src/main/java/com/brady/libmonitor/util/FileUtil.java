package com.brady.libmonitor.util;

import android.os.Environment;


import com.brady.libmonitor.MonitorManager;
import com.brady.libutil.data.StringUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by zyb
 *
 * @date 2017/8/31
 * @description 文件操作工具类
 */
public class FileUtil {

    /**回车换行*/
    public final static String NEW_LINE_SYMBOL = "\r\n";

    /**SD卡地址*/
    public static final String SDCARD_FOLDER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() ;
    /**应用缓存地址*/
    public static final String APP_CACHE_FOLDER_PATH = FileUtil.getAppCacheFilesDir().getAbsolutePath();
    /**
     * 获取应用缓存路径
     * @return
     */
    public static File getAppCacheFilesDir() {
        return MonitorManager.instance().getApplication().getExternalCacheDir();
    }


    /**
     * 通过递归得到某一路径下所有的目录及其文件
     * @param folderFilePath
     */
    public static ArrayList<String> getFiles(String folderFilePath){
        ArrayList<String> fileList = new ArrayList<String>();
        if(StringUtil.isNotEmpty(folderFilePath)) {
            File folderFile = new File(folderFilePath);
            if(folderFile.exists()&&folderFile.isDirectory()){
                File[] files = folderFile.listFiles();
                if(files!=null&&files.length>0){
                    for(File file:files){
                        if(file.isFile()){
                            fileList.add(file.getAbsolutePath());
                        }
                    }
                }
            }
        }
        return fileList;
    }

    /**
     * 删除文件
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        boolean result = false;
        if (dir == null) {
            MonitorUtil.prtLog("deleteDir null");
            return result;
        }
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                if (children != null) {
                    for (int i=0; i<children.length; i++) {
                        boolean success = deleteDir(new File(dir, children[i]));
                        if (!success) {
                            return false;
                        }
                    }
                }
                result = dir.delete();
            } else {
                result = dir.delete();
            }
        } catch (Exception e) {
            MonitorUtil.prtLog(e.getLocalizedMessage());
        }

        return result;
    }

    /**
     * 复制文件
     * @param fromFile
     * @param toFile
     */
    public static void copyFile(File fromFile, File toFile) {
        FileInputStream fisfrom = null;
        if (fromFile != null && fromFile.exists()&&fromFile.isFile()) {
            try {
                copyFile(new FileInputStream(fromFile), toFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            MonitorUtil.prtLog("err", "File not found!");
        }
    }

    /**
     * 复制文件
     * @param fromIns
     * @param toFile
     */
    public static void copyFile(InputStream fromIns, File toFile) {
        FileOutputStream fosto = null;
        try {
            if (toFile.exists()) {
                toFile.delete();
            }else{
                toFile.getParentFile().mkdirs();
            }
            fosto = new FileOutputStream(toFile);
            byte buffer[] = new byte[1024];
            int read = 0;
            while ((read = fromIns.read(buffer)) > 0) {
                fosto.write(buffer, 0, read); // 将内容写到新文件当中
            }
            fosto.flush();
        } catch (Exception e) {
            MonitorUtil.prtLog(e.getLocalizedMessage());
        }finally {
            try {
                if (fromIns != null) {
                    fromIns.close();
                }
            } catch (Exception e) {
            }
            try {
                if (fosto != null) {
                    fosto.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 存储String为文件
     * @param filePath 文件路径
     * @param data 文件内容
     */
    public static boolean save2File(String filePath, String data) {
        boolean saveFlag = false;
        File targetFile = new File(filePath);
        File dir = targetFile;
        if(!targetFile.isDirectory()) {
            dir = targetFile.getParentFile();
        }
        boolean isSuccess = dir.exists()||dir.mkdirs();

        if(isSuccess){
            OutputStreamWriter output = null;
            BufferedReader buffer = null;
            try {
                output = new OutputStreamWriter(new FileOutputStream(targetFile, false),"utf-8");
                buffer = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));

                String line;
                while((line = buffer.readLine()) != null) {
                    output.write(line);
                    output.write(NEW_LINE_SYMBOL);
                }
                output.flush();
                saveFlag = true;
                MonitorUtil.prtLog(FileUtil.class.getSimpleName(),"File create success!");
            } catch (Exception e) {
                MonitorUtil.prtLog(e.getLocalizedMessage());
                saveFlag = false;
            } finally {
                if(output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        MonitorUtil.prtLog(e.getLocalizedMessage());
                        saveFlag = false;
                    }
                }
                if(buffer != null) {
                    try {
                        buffer.close();
                    } catch (IOException e) {
                        MonitorUtil.prtLog(e.getLocalizedMessage());
                        saveFlag = false;
                    }
                }
            }
        } else {
            MonitorUtil.prtLog(FileUtil.class.getSimpleName(),"File create fail!");
        }
        return saveFlag;
    }


    /**
     * 获取文件夹大小
     * @param folderFilePath
     * @return long   返回的是字节长度，1M=1024k=1048576字节
     */
    public static long getFileSize(String folderFilePath) {
        if (StringUtil.isNotEmpty(folderFilePath)) {
           return getFileSize(new File(folderFilePath));
        }
        return 0;
    }

    /**
     * 获取文件夹大小
     * @param file
     * @return long   返回的是字节长度，1M=1024k=1048576字节
     */
    public static long getFileSize(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                return getDirectorySize(file);
            } else {
                return getSize(file);
            }
        }
        return 0;
    }

    /**
     * 获取指定文件夹
     * @param f
     * @return
     * @throws Exception
     *
     */
    private static long getDirectorySize(File f){
        long size = 0;
        File fList[] = f.listFiles();
        for (int i = 0; i < fList.length; i++) {
            if (fList[i].isDirectory()) {
                size = size + getDirectorySize(fList[i]);
            } else {
                size = size + getSize(fList[i]);
            }
        }
        return size;
    }


    /**
     * 获取指定文件大小
     * @param file
     * @return
     * @throws Exception 　　
     */
    private static long getSize(File file) {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis;
            try{
                fis = new FileInputStream(file);
                size = fis.available();
            }catch (Exception e){
            }
        }
        return size;
    }

    /**
     *
     * @param fileSize 文件字节
     * @return
     */
    public static int fileSize2MB(long fileSize){
        int sizeMB = 0;
        if(fileSize>0){
            try{
                sizeMB = Long.valueOf(fileSize/1048576).intValue();
            }catch (Exception e){

            }
        }
        //返回的是字节长度，1M=1024k=1048576字节 也就是if(fileSize<5*1048576)就好了
        return sizeMB;
    }

    /**
     * 把字节数B转化为KB、MB、GB的方法
     * @param size
     * @return
     */
    public static String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }
}
