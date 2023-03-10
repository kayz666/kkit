package com.github.kkit.scp.adapter.tcp.codec;

/**
 * 应答类型
 * @Author: kayz
 * @Date: 2018/12/5 17:07
 * @Version 1.0
 */
public enum AckType {
    REQUEST( (short) 0, "请求"),
    RESPONSE( (short)1, "响应"),
    RESPONSE_CONFIRM( (short)2, "响应确认"),  //
    RE_RESPONSE_CONFIRM((short)3,"")
    ;

    private short val;
    private String name;

    AckType(short val, String name){
        this.val=val;
        this.name=name;
    }

    public short getVal() {
        return val;
    }


    public String getName() {
        return name;
    }

    public static AckType getByValue(int value){
        for (AckType c : values()){
            if (c.getVal() == value){
                return c;
            }
        }
        return null;
    }

    public AckType getReBack(){
        if ((this.getVal() + 1) > 3){
            return RE_RESPONSE_CONFIRM;
        }
        return getByValue(this.getVal()+1);
    }
}
