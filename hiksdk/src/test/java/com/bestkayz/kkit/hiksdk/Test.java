package com.bestkayz.kkit.hiksdk;

import com.alibaba.fastjson.JSONObject;
import com.bestkayz.kkit.hiksdk.lib.FMSGCallBack;
import com.bestkayz.kkit.hiksdk.lib.FMSGCallBack_V31;
import com.bestkayz.kkit.hiksdk.lib.HCNetSDK;
import com.bestkayz.kkit.hiksdk.lib.HikEventCallBack;
import com.bestkayz.kkit.hiksdk.provider.HikSDKTool;
import com.bestkayz.kkit.hiksdk.provider.impl.HikAspectImpl;
import com.bestkayz.kkit.hiksdk.provider.HikSDKProvider;
import com.bestkayz.kkit.hiksdk.provider.impl.HikSDKProviderImpl;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author: Kayz
 * @create: 2022-02-14
 **/
@Slf4j
public class Test {

    public static void main(String[] args) {

        HikEventCallBack hikCallBack = new HikEventCallBack() {
            @Override
            public void AlarmDataHandle(String deviceInfo, int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
                log.info(deviceInfo);
            }
        };
        try {
            HikSDKProvider hikSDKProvider = HikSDKTool.builder("192.168.1.116","Admin12345",hikCallBack,"12").build();

            log.info(JSONObject.toJSONString(hikSDKProvider.getDeviceInfo()));
            log.info(JSONObject.toJSONString(hikSDKProvider.getTime()));
            log.info(JSONObject.toJSONString(hikSDKProvider.getAllUserCard()));
            log.info(JSONObject.toJSONString(hikSDKProvider.queryDeviceEmployee("10003")));
            log.info(JSONObject.toJSONString(hikSDKProvider.queryPersonFace("10003")));
        }catch (Exception e){
            log.warn("",e);
        }

    }

}
