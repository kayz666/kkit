package com.bestkayz.kkit.common.tools;


import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

/**
 * TypeConverter
 *
 * @author kayz
 */
public class TypeConverter {

    // 截取字节数组
    public static byte[] cutByteArr(byte[] data,int start, int len){
        if (start < 0 || len <= 0 || start >= data.length) return new byte[]{};
        if (len > (data.length-start)) len = data.length - start;
        byte[] temp = new byte[len];
        System.arraycopy(data,start,temp,0,len);
        return temp;
    }

    // 截取字节数组
    public static byte[] cutByteArr(byte[] data,int start){
        return cutByteArr(data,start,999999999);
    }

    //数组转十六进制字符串
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02X", new Integer(b & 0xff)));
        }
        return buf.toString();
    }

    public static String bytesToHexString(byte[] bytes, int len) {
        StringBuilder buf = new StringBuilder(len * 2);
        for (int i = 0; i < len; i++) {
            buf.append(String.format("%02X", new Integer(bytes[i] & 0xff)));
        }
        return buf.toString();
    }


    //十六进制字符串转数组
    public static byte[] hexStringToByteArray(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    //字符串转数组 UTF8编码
    public static byte[] stringToByteArray(String str) {
        try {
            if (str == null) {
                return new byte[0];
            }
            return str.getBytes("UTF8");
        } catch (Exception e) {
            return new byte[0];
        }

    }

    //数组转字符串UTF8编码
    public static String byteArrayToString(byte[] byteArray) {
        try {
            if (byteArray == null) {
                return null;
            }
            String str = new String(byteArray, "UTF8");
            return str;
        } catch (Exception e) {
            return null;
        }

    }

    public static String byteArrayToString(byte[] byteArray, int len) {
        try {
            if (byteArray == null) {
                return null;
            }
            byte[] tmp = new byte[len];
            System.arraycopy(byteArray, 0, tmp, 0, len);
            String str = new String(byteArray, "UTF8");
            return str;
        } catch (Exception e) {
            return null;
        }
    }

    public static String bytesToHex1(byte[] md5Array) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < md5Array.length; i++) {
            int temp = 0xff & md5Array[i];//
            String hexString = Integer.toHexString(temp);
            if (hexString.length() == 1) {//如果是十六进制的0f，默认只显示f，此时要补上0
                strBuilder.append("0").append(hexString);
            } else {
                strBuilder.append(hexString);
            }
        }
        return strBuilder.toString();
    }

    //通过java提供的BigInteger 完成byte->HexString
    public static String bytesToHex2(byte[] md5Array) {
        BigInteger bigInt = new BigInteger(1, md5Array);
        return bigInt.toString(16);
    }

    //通过位运算 将字节数组到十六进制的转换
    public static String bytesToHex3(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    /**
     * 字符串转... addby blue 2018/7/16
     */
    public static String getMultipleOf8(String str) {
        StringBuffer text = new StringBuffer();
        int len = str.getBytes(UTF_8).length;
        int resultLen = (len + 8) / 8 * 8;
        for (int i = 0; i < resultLen - len; i++) {
            text.append(' ');
        }
        return str + text;
    }


    /**
     * short到字节数组的转换.
     */
    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8;// 向右移8位
        }
        return b;
    }

    /**
     * 字节数组到short的转换.
     */
    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }


    /**
     * int到字节数组的转换.
     */
    public static byte[] intToByte(int number) {
        int temp = number;
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8;// 向右移8位
        }
        return b;
    }

    public static byte[] intToByteHi(int number){
        byte[] b = new byte[4];
        b[0] = (byte) ((number >> 24) & 0xFF);
        b[1] = (byte) ((number >> 16) & 0xFF);
        b[2] = (byte) ((number >> 8) & 0xFF);
        b[3] = (byte) (number & 0xFF);
        return b;
    }


    public static byte[] intToByteHi(long number){
        byte[] b = new byte[4];
        b[0] = (byte) ((number >> 24) & 0xFF);
        b[1] = (byte) ((number >> 16) & 0xFF);
        b[2] = (byte) ((number >> 8) & 0xFF);
        b[3] = (byte) (number & 0xFF);
        return b;
    }

    /**
     * 字节数组到int的转换.
     */
    public static int byteToInt(byte[] b) {
        int s = 0;
        int s0 = b[0] & 0xff;// 最低位
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        int s3 = b[3] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }

    public static int byteToIntHi(byte[] b){
        int s = 0;
        int s0 = b[3] & 0xff;// 最低位
        int s1 = b[2] & 0xff;
        int s2 = b[1] & 0xff;
        int s3 = b[0] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }

    /**
     * 翻转数组
     * @param data
     * @return
     */
    public static byte[] overturnBytes(byte[] data){
        byte[] temp = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            temp[i] = data[data.length-i-1];
        }
        return temp;
    }

    /**
     * long类型转成byte数组
     */
    public static byte[] longToByte(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位 temp = temp
            temp = temp >> 8;// 向右移8位
        }
        return b;
    }

    /**
     * 字节数组到long的转换.
     */
    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// 最低位
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }



    /**
     * 16进制字符串转Long
     *
     * @param hexStr 16进制字符串
     * @return 0L
     */
    public static Long hexStr2Long(String hexStr) {
        if (StringUtils.isEmpty(hexStr)) {
            return null;
        }
        return new BigInteger(hexStr, 16).longValue();
    }

    /**
     * 批量16进制字符串转Long
     *
     * @param hexStrs 16进制字符串
     * @return 列表
     */
    public static List<Long> hexStr2Longs(List<String> hexStrs) {
        if (hexStrs == null || hexStrs.size() == 0) {
            return null;
        }
        return hexStrs.stream().map(TypeConverter::hexStr2Long).collect(toList());
    }

    /**
     * Long转16进制字符串
     *
     * @param lg Long型值
     * @return hexStr
     */
    public static String long2hexStr(Long lg) {
        if (lg == null) {
            return null;
        }
        return new BigInteger(String.valueOf(lg)).toString(16).toUpperCase();
    }

    /**
     * 批量_Long转16进制字符串
     *
     * @param lgs Long型值
     * @return 列表
     */
    public static List<String> long2hexStrs(List<Long> lgs) {
        if (lgs == null || lgs.size() == 0) {
            return null;
        }
        return lgs.stream().map(TypeConverter::long2hexStr).collect(toList());
    }

    /**
     * double到字节数组的转换.
     */
    public static byte[] doubleToByte(double num) {
        byte[] b = new byte[8];
        long l = Double.doubleToLongBits(num);
        for (int i = 0; i < 8; i++) {
            b[i] = new Long(l).byteValue();
            l = l >> 8;
        }
        return b;
    }

    /**
     * 字节数组到double的转换.
     */
    public static double getDouble(byte[] b) {
        long m;
        m = b[0];
        m &= 0xff;
        m |= ((long) b[1] << 8);
        m &= 0xffff;
        m |= ((long) b[2] << 16);
        m &= 0xffffff;
        m |= ((long) b[3] << 24);
        m &= 0xffffffffl;
        m |= ((long) b[4] << 32);
        m &= 0xffffffffffl;
        m |= ((long) b[5] << 40);
        m &= 0xffffffffffffl;
        m |= ((long) b[6] << 48);
        m &= 0xffffffffffffffl;
        m |= ((long) b[7] << 56);
        return Double.longBitsToDouble(m);
    }


    /**
     * float到字节数组的转换.
     */
    public static void floatToByte(float x) {
        //先用 Float.floatToIntBits(f)转换成int
    }

    /**
     * 字节数组到float的转换.
     */
    public static float getFloat(byte[] b) {
        // 4 bytes
        int accum = 0;
        for (int shiftBy = 0; shiftBy < 4; shiftBy++) {
            accum |= (b[shiftBy] & 0xff) << shiftBy * 8;
        }
        return Float.intBitsToFloat(accum);
    }

    /**
     * char到字节数组的转换.
     */
    public static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    /**
     * 字节数组到char的转换.
     */
    public static char byteToChar(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }

    /**
     * string到字节数组的转换.
     */
    public static byte[] stringToByte(String str) throws UnsupportedEncodingException {
        return str.getBytes("UTF8");
    }

    /**
     * 字节数组到String的转换.
     */
    public static String bytesToString(byte[] str) {
        String keyword = null;
        try {
            keyword = new String(str,0, indexOfBytes(str,(byte)'\0'),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return keyword;
    }

    public static int indexOfBytes(byte[] str,byte chr){
        for (int i = 0; i < str.length; i++) {
            if (str[i] == chr){
                return i;
            }
        }
        return str.length;
    }

    /**
     * object到字节数组的转换
     */
    public void testObject2ByteArray() throws IOException,
            ClassNotFoundException {
        // Object obj = "";
        Integer[] obj = {1, 3, 4};

        // // object to bytearray
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(obj);
        byte[] bytes = bo.toByteArray();
        bo.close();
        oo.close();
        System.out.println(Arrays.toString(bytes));

        Integer[] intArr = (Integer[]) testByteArray2Object(bytes);
        System.out.println(Arrays.asList(intArr));


        byte[] b2 = intToByte(123);
        System.out.println(Arrays.toString(b2));

        int a = byteToInt(b2);
        System.out.println(a);

    }

    /**
     * 字节数组到object的转换.
     */
    private Object testByteArray2Object(byte[] bytes) throws IOException,
            ClassNotFoundException {
        // byte[] bytes = null;
        Object obj;
        // bytearray to object
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        ObjectInputStream oi = new ObjectInputStream(bi);
        obj = oi.readObject();
        bi.close();
        oi.close();
        System.out.println(obj);
        return obj;
    }


    public static String byteToCardNo(byte[] b){
        long s = 0;
        long s0 = b[3] & 0xff;// 最低位
        long s1 = b[2] & 0xff;
        long s2 = b[1] & 0xff;
        long s3 = b[0] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return ""+s;
    }

    public static byte bcdIntToByte(int d){
        return (byte) (d+(d/10)*6);
    }

    public static int  bcdByteToInt(byte d){
        int x = d;
        return x - (x/16)*6;
    }

}
