package com.bestkayz.kkit.common.tools;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author: Kayz
 * @create: 2020-10-12
 **/
public class CarderKeyUtil {

    private static String carderAuthInfoKey = "ykzl2018";

    /**
     *
     * @param companyCode 32位
     * @param createTime  正常创建时间  转换后必须是13位
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String[] generateKey(String companyCode, Date createTime) throws UnsupportedEncodingException {
        String timeStr = ""+createTime.getTime();
        String[] temp = new String[16];
        for (int i = 0; i < 16; i++) {
            StringBuilder x = new StringBuilder();
            for (int j = 0; j < 6; j++) {
                if ((i&1)==1){
                    x.append(companyCode, i+j, i+j+ 1).append(timeStr.substring(j*2, j*2+1));
                }else {
                    x.append(companyCode.substring(i+j, i+j+ 1)).append(timeStr.substring(5-j, 5-j+1));
                }

            }
            temp[i] = x.toString();
        }
        return temp;
    }

    public static String[] generateDefaultKey(){
        String[] temp = new String[16];
        for (int i = 0; i < 16; i++) {
            temp[i] = "FFFFFFFFFFFF";
        }
        return temp;
    }

    public static CipherText decryptKey(String data){
        String text = DESUtil.decryptBase64(data,carderAuthInfoKey);
        CipherText cipherText = JSONObject.parseObject(text,CipherText.class);
        long nowTime = System.currentTimeMillis();
        if (cipherText.timestamp<nowTime-200*60*1000 ||
            cipherText.timestamp>nowTime+200*60*1000){
            throw new IllegalArgumentException("密文过期");
        }
        return cipherText;
    }

    public static CipherText decryptKeyLazy(String data){
        String text = DESUtil.decryptBase64(data,carderAuthInfoKey);
        CipherText cipherText = JSONObject.parseObject(text,CipherText.class);
        return cipherText;
    }


    public static String encryptKey(CipherText cipherText) throws UnsupportedEncodingException {
        String str = JSONObject.toJSONString(cipherText);
        System.out.println(str);
        return DESUtil.encryptBase64(str,carderAuthInfoKey);
    }

    @Data
    public static class CipherText implements Serializable{

        private M1CardKey cardKey;

        private Long timestamp;

        private String companyCode;
    }

    @Data
    public static class M1CardKey implements Serializable {

        // 使用的扇区顺序 0~15
        private Integer[] sectors;

        // 按扇区对应的扇区密钥
        private String[] keyA;

        private String[] keyB;

    }

}
