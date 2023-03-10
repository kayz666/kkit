package com.github.kkit.scp.adapter.tcp.codec;

/**
 * @Author: kayz
 * @Date: 2018/11/21 9:40
 * @Version 1.0
 */
public enum PayloadType {
    BYTES( (short)0, "bytes"),  //解析成byte[]
    //STRING( (short)1, "String"),   //解析成String
    ERROR( (short)2,"Error msg")  //错误消息
    ;

    private short val;
    private String name;

    PayloadType(short val, String name){
        this.val=val;
        this.name=name;
    }

    public short getVal() {
        return val;
    }


    public String getName() {
        return name;
    }

    public static PayloadType getByValue(int value){
        for (PayloadType c : values()){
            if (c.getVal() == value){
                return c;
            }
        }
        return null;
    }
}
