package com.bestkayz.kkit.hiksdk.provider;

import com.bestkayz.kkit.hiksdk.lib.FMSGCallBack;
import com.bestkayz.kkit.hiksdk.lib.FMSGCallBack_V31;
import com.bestkayz.kkit.hiksdk.lib.HCNetSDK;
import com.bestkayz.kkit.hiksdk.lib.HikEventCallBack;
import com.bestkayz.kkit.hiksdk.provider.impl.HikAspectImpl;
import com.bestkayz.kkit.hiksdk.provider.impl.HikSDKProviderImpl;
import com.sun.jna.Pointer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Calendar;
import java.util.Date;

/**
 * @author: Kayz
 * @create: 2021-01-20
 **/
public class HikSDKTool {



    public static HikSDKProviderImpl builder(String host, String password,HikEventCallBack hikCallBack, String deviceId){
        HikSDKProviderImpl hikSDKProviderImpl = HikSDKProviderImpl.getInstance(deviceId);
        hikSDKProviderImpl
                .setIp(host)
                .setPassword(password);
        if (hikCallBack != null) {
            hikSDKProviderImpl
                    .setFMSFCallBack(new FMSGCallBack(hikCallBack,deviceId))
                    .setFmsgCallBack_v31(new FMSGCallBack_V31(hikCallBack,deviceId));
        }
        return hikSDKProviderImpl;
    }

    public static Date convertTime(HCNetSDK.NET_DVR_TIME strTime){
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                strTime.dwYear,
                strTime.dwMonth-1,
                strTime.dwDay,
                strTime.dwHour,
                strTime.dwMinute,
                strTime.dwSecond
        );
        return calendar.getTime();
    }

    public static byte[] convertPicture(int pPicSize,Pointer pPicData){
        HCNetSDK.BYTE_ARRAY ptrByteArray = new HCNetSDK.BYTE_ARRAY(pPicSize);
        ptrByteArray.write();
        Pointer pointer = ptrByteArray.getPointer();
        pointer.write(0,pPicData.getByteArray(0,ptrByteArray.size()),0,ptrByteArray.size());
        ptrByteArray.read();
        return ptrByteArray.byValue;
    }


}
