package com.bestkayz.kkit.hiksdk.provider.impl;

import com.bestkayz.kkit.hiksdk.provider.impl.HikSDKProviderImpl;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author: Kayz
 * @create: 2021-01-14
 **/
@Slf4j
public class HikAspectImpl implements InvocationHandler {

    private HikSDKProviderImpl providerImpl;

    public HikAspectImpl(HikSDKProviderImpl realSubject) {
        this.providerImpl = realSubject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("release")){
            return method.invoke(providerImpl,args);
        }
        if (!providerImpl.isLogin()){
            providerImpl.login();
            providerImpl.setLog("C:\\SdkLog\\");
        }
        try {
            providerImpl.lock();
            Object returnValue=method.invoke(providerImpl,args);
            return returnValue;
        }catch (Exception e){
            throw e;
        }finally {
            providerImpl.unlock();
        }
    }
}
