package com.bestkayz.kkit.hiksdk.provider;


import com.bestkayz.kkit.common.core.base.Result;
import com.bestkayz.kkit.hiksdk.lib.HCNetSDK;
import com.bestkayz.kkit.hiksdk.provider.impl.HikSDKProviderImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author : kayz
 * @Date : 2020/5/30
 * @Version 1.0
 */
public interface HikSDKProvider {

    HikSDKProviderImpl addDeviceProperty(String key, Object data);

    Object getDeviceProperty(String key);

    String getDeviceId();

    void init();

    HCNetSDK.NET_DVR_DEVICECFG_V40 getDeviceInfo();

    // 获取设备时间
    Date getTime();

    // 设置设备时间 （校时
    Result setTime(Date date);

    Result addPersonInfo(String personId,String name);

    Result addPersonCard(String personId,String cardNo);

    Result addPersonFace(String personId,byte[] face);

    Result delUserInfo(String strEmployeeID);

    Result<List<Map<String,Object>>> getRecord(long startTime, long endTime,int count);

    Result openDoor();

    void release();

    void setLog(String path);

    Result delOneCard(String strCardNo);

    Result delOneFace(String strCardNo);

    Result setOneCard(String strCardNo, String sno, String name);

    Result setOneFace(String strCardNo, byte[] face);

    Result<List<String>> getAllCard();

    Result<byte[]> getOneFace(String strCardNo);

    Result queryDeviceEmployee(String sno);
}
