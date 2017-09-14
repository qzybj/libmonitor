package com.brady.libmonitor.model;


import com.brady.libutil.data.StringUtil;

/**
 * Created by zyb
 *
 * @date 2017/9/11
 * @description cpu信息
 */
public class CpuInfo {
    //用户程序的CPU占比
    private String userPercent = null ;
    //系统程序的CPU占比
    private String sysPercent = null ;
    //当前App的CPU占比 可能会比用户的大
    private String curAppPercent = null ;


    /**
     * 是否没有取到CPU信息
     * @return
     */
    public boolean isNull(){
        if(StringUtil.isEmpty(userPercent) &&
                StringUtil.isEmpty(sysPercent) &&
                StringUtil.isEmpty(curAppPercent)){
            return true ;
        }
        return false ;
    }

    public String getUserPercent() {
        return userPercent;
    }

    public void setUserPercent(String userPercent) {
        this.userPercent = userPercent;
    }

    public String getSysPercent() {
        return sysPercent;
    }

    public void setSysPercent(String sysPercent) {
        this.sysPercent = sysPercent;
    }

    public String getCurAppPercent() {
        return curAppPercent;
    }

    public void setCurAppPercent(String currAppPercent) {
        this.curAppPercent = currAppPercent;
    }

}
