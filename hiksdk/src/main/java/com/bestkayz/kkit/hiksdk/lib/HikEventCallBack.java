package com.bestkayz.kkit.hiksdk.lib;

import com.sun.jna.Pointer;

/**
 * @author: Kayz
 * @create: 2022-02-14
 **/
public interface HikEventCallBack {

    void AlarmDataHandle(String deviceInfo, int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser);

}
