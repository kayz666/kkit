package com.bestkayz.kkit.common.tools;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * DESUtil
 *
 * @author kayz
 */
public class DESUtil {

    private DESUtil() {
    }

    /**
     * 加密
     */
    public static byte[] encrypt(byte[] datasource, String password) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());

            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);

            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            //用密匙初始化Cipher对象,ENCRYPT_MODE用于将 Cipher 初始化为加密模式的常量
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);

            //现在，获取数据并加密
            //正式执行加密操作
            return cipher.doFinal(datasource); //按单部分操作加密或解密数据，或者结束一个多部分操作

        } catch (Exception e) {
            throw new RuntimeException(password + "DES加密错误 " + e.getMessage());
        }
    }

    /**
     * 加密 addby blue 2018/7/16
     */
    public static String encrypt(String orgStr, String password) {
        byte[] orgByt = TypeConverter.getMultipleOf8(orgStr).getBytes(UTF_8);
        byte[] resByt = encrypt(orgByt, TypeConverter.getMultipleOf8(password));
        return TypeConverter.bytesToHexString(resByt);

    }

    public static String encryptBase64(String orgStr,String password) throws UnsupportedEncodingException {
        byte[] orgByt = TypeConverter.getMultipleOf8(orgStr).getBytes(UTF_8);
        byte[] resByt = encrypt(orgByt, TypeConverter.getMultipleOf8(password));
        return Base64Util.bytesToStringSafe(resByt);
    }


    public static String encryptBase64NoSafe(String orgStr,String password) throws UnsupportedEncodingException {
        byte[] orgByt = TypeConverter.getMultipleOf8(orgStr).getBytes(UTF_8);
        byte[] resByt = encrypt(orgByt, TypeConverter.getMultipleOf8(password));
        return Base64Util.bytesToString(resByt);
    }

    /**
     * 解密
     */
    public static byte[] decrypt(byte[] src, String password) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(password.getBytes(UTF_8));
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");//返回实现指定转换的 Cipher 对象
            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey securekey = keyFactory.generateSecret(desKey);

            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);

            // 真正开始解密操作
            return cipher.doFinal(src);
        } catch (Throwable e) {
            throw new RuntimeException(password + "DES解密错误 " + e.getMessage());
        }
    }

    /**
     * 解密 addby blue 2018/7/16
     */
    public static String decrypt(String orgStr, String password) {
        try {
            byte[] resByt = decrypt(TypeConverter.hexStringToByteArray(orgStr), TypeConverter.getMultipleOf8(password));
            return new String(resByt, UTF_8).trim();
        } catch (Exception e) {
            throw new IllegalArgumentException(password + "DES解密错误 " + e.getMessage());
        }
    }

    public static String decryptBase64(String orgStr,String password){
        try {
            byte[] resByt = decrypt(Base64Util.stringToBytesSafe(orgStr), TypeConverter.getMultipleOf8(password));
            return new String(resByt, UTF_8).trim();
        } catch (Exception e) {
            throw new IllegalArgumentException(password + "DES解密错误 " + e.getMessage());
        }
    }

    public static String decryptBase64NoSafe(String orgStr,String password){
        try {
            byte[] resByt = decrypt(Base64Util.stringToBytes(orgStr), TypeConverter.getMultipleOf8(password));
            return new String(resByt, UTF_8).trim();
        } catch (Exception e) {
            throw new IllegalArgumentException(password + "DES解密错误 " + e.getMessage());
        }
    }
}

