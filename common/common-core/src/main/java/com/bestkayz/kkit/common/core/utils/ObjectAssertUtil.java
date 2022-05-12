package com.bestkayz.kkit.common.core.utils;


import com.bestkayz.kkit.common.core.base.BaseException;
import com.bestkayz.kkit.common.core.exceptions.common.ObjectNotFindException;

/**
 * @author: Kayz
 * @create: 2020-07-14
 **/
public class ObjectAssertUtil {

    public static  <T> T notNull(T obj, String msg){
        if (obj == null) {
            throw new ObjectNotFindException(msg);
        }
        return obj;
    }

    public static <T> T notNull(T obj, BaseException baseException){
        if (obj == null) {
            throw baseException;
        }
        return obj;
    }

    public static void delay(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
