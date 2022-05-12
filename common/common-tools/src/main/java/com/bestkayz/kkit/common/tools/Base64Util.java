package com.bestkayz.kkit.common.tools;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Util {

    public static String bytesToString(byte[] bytes) throws UnsupportedEncodingException {
        return new String(Base64.getEncoder().encode(bytes),"UTF-8");
    }
    public static byte[] stringToBytes(String s) throws UnsupportedEncodingException {
        return Base64.getDecoder().decode(s.getBytes("UTF-8"));
    }


    public static byte[] stringToBytesSafe(String s) throws UnsupportedEncodingException {
        return Base64.getUrlDecoder().decode(s.getBytes("UTF-8"));
    }

    public static String bytesToStringSafe(byte[] bytes) throws UnsupportedEncodingException {
        return new String(Base64.getUrlEncoder().encode(bytes),"UTF-8");
    }
}
