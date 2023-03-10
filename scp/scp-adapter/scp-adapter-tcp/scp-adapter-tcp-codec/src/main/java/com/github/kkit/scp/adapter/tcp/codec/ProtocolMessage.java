package com.github.kkit.scp.adapter.tcp.codec;


import com.github.kkit.scp.common.TypeConverter;
import com.github.kkit.scp.core.ScpMessage;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @Author: kayz
 * @Date: 2018/12/5 9:43
 * @Version 1.0
 */
@Setter
@Getter
public class ProtocolMessage implements ScpMessage {

    private static final Random random = new Random();

    /**
     * 控制帧
     * 长度: 2字节
     */
    private ControlStructure controlStructure ;

    /**
     * 会话ID
     */
    private int sessionId;

    /**
     * 功能码
     */
    private short funCode;

    /**
     * 包ID
     */
    private short packId;

    /**
     * 包大小
     */
    private short packSize;

    /**
     * 消息体
     */
    private byte[] payload;

    private short checkout;

    public ProtocolMessage() {
    }

    public ProtocolMessage(int sessionId , AckType ackType, PayloadType payloadType, short funCode , short packId, short packSize, byte[] payload){
        this.sessionId = sessionId;
        this.controlStructure = new ControlStructure();
        this.controlStructure.setAck(ackType);
        this.controlStructure.setPackage_flag(true);
        this.controlStructure.setMsg_type(payloadType);
        this.funCode = funCode;
        this.packId = packId;
        this.packSize = packSize;
        this.payload = payload;
    }

    @Override
    public String sessionId() {
        return String.valueOf(sessionId);
    }

    @Override
    public String getRouter() {
        return TypeConverter.shortToHexString(funCode);
    }

    @Override
    public Object getData() {
        return payload;
    }

    public static ProtocolMessage newMessage(short funCode , String payload){
        return newMessage(
                funCode,
                AckType.REQUEST,
                payload
        );
    }

    public static ProtocolMessage newMessage(short funCode , AckType ackType, String payload){
        return new ProtocolMessage(
                random.nextInt(Integer.MAX_VALUE),
                ackType,
                PayloadType.BYTES,
                funCode,
                (short) 0,(short)0,
                payload!=null?payload.getBytes(StandardCharsets.UTF_8):null
        );
    }


    public short getControlStrShort(){
        return this.controlStructure.getControlStrShort();
    }

    public int getMessageLength(){
        return  (payload!=null?payload.length:0)+(controlStructure.isPackage_flag()?4:0);
    }




    public String getConvertStrPayload(){
        return new String(payload, StandardCharsets.UTF_8);
    }


    @Getter
    @Setter
    public static class ControlStructure {

        // 协议版本号
        // bit[0:2]
        private int version = 0;

        // 消息类型
        // bit[8:10]
        private PayloadType msg_type;

        // 应答标记
        // bit[11:13]
        private AckType ack = AckType.REQUEST;

        // 加密标记
        // bit[14]
        private boolean encryption = true;

        // 多包标记
        // bit[15]
        private boolean package_flag = false;


        public static ControlStructure getCtrl(short ctrl){
            ControlStructure controlStructure = new ControlStructure();
            controlStructure.package_flag = (ctrl&0x01)==1;
            controlStructure.encryption = (ctrl>>1&0x01) ==1;
            controlStructure.ack = AckType.getByValue(ctrl>>2&0x07);
            controlStructure.msg_type = PayloadType.getByValue(ctrl>>5&0x07);
            return controlStructure;
        }

        public short getControlStrShort(){
            return (short) (
                    (this.package_flag?(short)1:(short)0) +
                            ((this.encryption?(short)1:(short)0)<<1) +
                            (this.ack.getVal()<<2)+
                            (this.msg_type.getVal()<<5)
            );
        }


    }

}
