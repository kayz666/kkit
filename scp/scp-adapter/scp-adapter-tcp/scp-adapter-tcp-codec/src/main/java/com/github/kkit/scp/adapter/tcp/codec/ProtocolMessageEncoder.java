package com.github.kkit.scp.adapter.tcp.codec;

import com.github.kkit.scp.common.TypeConverter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @Author: kayz
 * @Date: 2018/12/5 9:40
 * @Version 1.0
 */
//@Slf4j
@ChannelHandler.Sharable
public class ProtocolMessageEncoder extends MessageToByteEncoder<ProtocolMessage> {

    private static final Logger log = LoggerFactory.getLogger("SCP");


    // 通讯密码
    private byte[] password;


    public ProtocolMessageEncoder(byte[] password) {
        this.password = password;
    }


    @Override
    public void encode(ChannelHandlerContext ctx, ProtocolMessage msg, ByteBuf out) throws Exception{
        if (password == null){
            msg.getControlStructure().setEncryption(false);
        }
        out.writeShort(ProtocolMessageDecoder.MESSAGE_HEAD);   //帧头    2字节
        out.writeInt(msg.getMessageLength()+10);        //长度    4字节
        out.writeShort(msg.getControlStrShort());       //控制位  2字节
        out.writeInt(msg.getSessionId());               //会话Id  4字节
        out.writeShort(msg.getFunCode());               //功能码  2字节
        if (msg.getControlStructure().isPackage_flag()){
            out.writeShort(msg.getPackId());            //包序号  2字节
            out.writeShort(msg.getPackSize());          //包大小  2字节
        }
        if (msg.getPayload() != null) {
            out.writeBytes(msg.getPayload());               //数据体  n字节
        }
        short check = VerifyMessage.checkSum(out,out.writerIndex());
        out.writeShort(check);                          //校验位  2字节
        if (msg.getControlStructure().isEncryption()){
            VerifyMessage.xor(password,out,msg.getControlStructure().isPackage_flag()?18:14,msg.getPayload()==null?0:msg.getPayload().length);
        }
        if (log.isDebugEnabled() && (msg.getFunCode() != (short) 0xF182)) {
            log.debug("SCP发送 远程地址:" + ctx.channel().remoteAddress() +
                    " 消息功能码:" + TypeConverter.bytesToHexString(TypeConverter.shortToByte(msg.getFunCode())) +
                    " 数据体长度:" + (msg.getPayload()==null?0:msg.getPayload().length) +
                    " 数据:" + (msg.getPayload()==null?"":(msg.getPayload().length<200?TypeConverter.bytesToHexString(msg.getPayload()):TypeConverter.bytesToHexString(msg.getPayload(),200)))
            );
        }
    }
}
