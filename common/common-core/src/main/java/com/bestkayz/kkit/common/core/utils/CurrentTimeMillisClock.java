package com.bestkayz.kkit.common.core.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author : kayz
 * @Date : 2019/11/15
 * @Version 1.0
 */
public class CurrentTimeMillisClock {
    private static CurrentTimeMillisClock instance;
    private static boolean flag = true;
    private volatile long now;
    private CurrentTimeMillisClock() {
        this.now = System.currentTimeMillis();
        scheduleTick();
    }
    private void scheduleTick() {
        new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread thread = new Thread(runnable, "current-time-millis");
            thread.setDaemon(true);
            return thread;
        }).scheduleAtFixedRate(() -> {
            now = System.currentTimeMillis();
        }, 1, 1, TimeUnit.MILLISECONDS);
    }
    public long now() {
        return now;
    }
    public static CurrentTimeMillisClock getInstance() {
        if (flag){
            synchronized (CurrentTimeMillisClock.class) {
                if (flag){
                    instance = new CurrentTimeMillisClock();
                    flag = false;
                }
            }
        }
        return instance;
    }
}