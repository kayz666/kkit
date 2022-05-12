package com.bestkayz.kkit.common.tools;

/**
 * @author: Kayz
 * @create: 2020-07-23
 **/
public class PasswordUtil {

    public static String  getPasswordOrDefault(String identityNo){
        if (identityNo == null){
            identityNo = "000000";
        }
        if (identityNo.length()<6){
            identityNo = "000000"+identityNo;
        }
        identityNo = identityNo.replaceAll("[a-zA-Z]","0");
        return identityNo.substring(identityNo.length()-6);
    }

}
