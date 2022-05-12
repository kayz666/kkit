package com.bestkayz.kkit.hiksdk.lib;

import com.sun.jna.Pointer;

/**
 * @author: Kayz
 * @create: 2021-01-14
 **/
public class FMSGCallBack implements HCNetSDK.FMSGCallBack{

    private HikEventCallBack hikCallBack;

    private String deviceInfo;

    public FMSGCallBack(HikEventCallBack hikCallBack, String deviceInfo) {
        this.hikCallBack = hikCallBack;
        this.deviceInfo = deviceInfo;
    }

    @Override
    public void invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        hikCallBack.AlarmDataHandle(deviceInfo,lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
    }
}
