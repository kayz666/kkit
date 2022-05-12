package com.bestkayz.kkit.common.tools;


import static com.bestkayz.kkit.common.tools.TypeConverter.byteToLong;
import static com.bestkayz.kkit.common.tools.TypeConverter.longToByte;

/**
 * @author: Kayz
 * @create: 2020-07-14
 **/
public class CardNoUtil {

    /**
     * 十进制卡片序列号反转
     * @return
     */
    public static Long cardSnConvert(Long cardSn){
        byte[] temp = longToByte(cardSn);
        byte[] temp2 = new byte[8];
        for (int i = 0; i < 4; i++) {
            temp2[i] = temp[3-i];
        }
        for (int i = 0; i < 4; i++) {
            temp2[i+4] = 0x00;
        }
        return byteToLong(temp2);
    }


    public static String cardSnConvert(String cardSn){
        return ""+cardSnConvert(Long.valueOf(cardSn));
    }


    public static String cardSnConvertAndFill(String cardSn){
        String cardno = cardSnConvert(cardSn);
        if (cardno.length()<10){
            for (int i = 0; i < (10-cardno.length()); i++) {
                cardno = "0"+cardno;
            }
        }
        return cardno;
    }

    /**
     * 十六进制卡号转十进制卡号
     * @param hexCardNo
     * @return
     */
    public static String hexCardNoToDecCardNo(String hexCardNo){
        return String.valueOf(TypeConverter.hexStr2Long(hexCardNo));
    }

    public static String decCardNoToHexCardNo(String decCardNo){
        byte[] bytes = TypeConverter.intToByteHi(Long.valueOf(decCardNo));
        return TypeConverter.bytesToHexString(bytes);
    }

}
