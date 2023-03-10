package com.github.kkit.scp.adapter.tcp.codec;


import com.github.kkit.scp.common.TypeConverter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @Author: kayz
 * @Date: 2018/12/6 12:17
 * @Version 1.0
 */
//@ChannelHandler.Sharable
public class ProtocolMessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger log = LoggerFactory.getLogger("SCP");

    public static final short MESSAGE_HEAD = (byte)0xFCFE;

    // 通讯密码
    private byte[] password;

    public ProtocolMessageDecoder(int maxMessageLength, byte[] password){
        super(maxMessageLength,2,4);
        this.password = password;
    }


    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception{
//        byte[] temp1 = new byte[in.readableBytes()];
//        in.readBytes(temp1);
//        log.info("收到111:"+TypeConverter.bytesToHexString(temp1));
        ByteBuf frame = (ByteBuf) super.decode(ctx,in);
        if (frame == null){
//            if (in.writerIndex() !=0){
//                //logger.debug("--------------"+in.writerIndex()+"||||||||||");
//                in.resetWriterIndex();
//                throw new SnakeMessageException("message is incomplete");
//            }
            return null;
        }
//        byte[] temp = new byte[frame.readableBytes()];
//        frame.readBytes(temp);
//        log.info("收到:"+TypeConverter.bytesToHexString(temp));
        short header = frame.readShort();
        if (header != MESSAGE_HEAD){
            //包头不对
            log.warn("数据包头解析不匹配 HEAD:" + header);
            in.resetWriterIndex();
            return null;
            //throw new SnakeMessageDecoderException("protocol message header error");
        }
        int length = frame.readInt();
        ProtocolMessage protocolMessage = new ProtocolMessage();
        ProtocolMessage.ControlStructure controlStructure = ProtocolMessage.ControlStructure.getCtrl(frame.readShort());
        protocolMessage.setSessionId(frame.readInt());
        protocolMessage.setFunCode(frame.readShort());
        if (controlStructure.isPackage_flag()){
            protocolMessage.setPackId(frame.readShort());
            protocolMessage.setPackSize(frame.readShort());
        }
        int off = controlStructure.isPackage_flag()?18:14;
        if (controlStructure.isEncryption()){
            VerifyMessage.xor(password,frame,off,length-(controlStructure.isPackage_flag()?14:10));
        }
        short check = VerifyMessage.checkSum(frame,length+4);
        if (check != frame.getShort(length+4)){
            //throw new SnakeMessageDecoderException("protocol message check error");
            log.error("protocol message password check error");
            return null;
        }
        protocolMessage.setControlStructure(controlStructure);
        ByteBuf payload = frame.slice(off,length+4-off);
        byte[] arr= new byte[payload.writerIndex()]; //length+4-off-2   35+4-8
        payload.getBytes(0,arr);
        protocolMessage.setPayload(arr);
        //logger.debug("Payload::"+TypeConverter.bytesToHexString(arr));
        //in.resetWriterIndex();
        ReferenceCountUtil.release(frame);
        if (log.isDebugEnabled() && (protocolMessage.getFunCode() != (short) 0xF182)) {
            log.debug("SCP接收 远程地址:" + ctx.channel().remoteAddress() +
                    " 消息功能码:" + TypeConverter.bytesToHexString(TypeConverter.shortToByte(protocolMessage.getFunCode())) +
                    " 数据体长度:" + (protocolMessage.getPayload()==null?0:protocolMessage.getPayload().length) +
                    " 数据:" + (arr.length<200?TypeConverter.bytesToHexString(arr):TypeConverter.bytesToHexString(arr,200))
            );
        }
        return protocolMessage;
    }
}
