package com.bestkayz.kkit.common.tools;

import java.util.UUID;

/**
 * File: UUIDUtils.java
 * Author: kayz
 * Version: 1.1
 * Create: 2018/9/26 11:30
 **/
public class UUIDUtils {

    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static Long getUuidOfLong(){
        String s = getUUID();
        return TypeConverter.byteToLong(TypeConverter.hexStringToByteArray(s));
    }
}
