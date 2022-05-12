package com.bestkayz.kkit.common.tools;

/**
 * @author: Kayz
 * @create: 2022-02-14
 **/
public class StringUtils {

    public static boolean isEmpty(String str){
        if (str == null || str.equals("")){
            return true;
        }
        return false;
    }
}
