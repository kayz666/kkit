package com.github.kkit.scp.adapter.tcp.codec;

import io.netty.buffer.ByteBuf;

/**
 * @Author: kayz
 * @Date: 2018/11/21 9:21
 * @Version 1.0
 */
public class VerifyMessage {

    public static byte[] xor(byte[] password,byte[] msg){
        xor(password,msg,0,msg.length);
        return msg;
    }

    /**
     * 异或计算
     * @param password 密码
     * @param msg 数据
     * @param off 偏移
     * @param mlen 需要计算的长度
     */
    public static void xor(byte[] password,byte[] msg,int off,int mlen){
        int plen=password.length;
        if (mlen > plen){
            for (int i=0;i<(mlen/plen);i++){
                for (int j=0;j<plen;j++){
                    msg[i*plen+j+off]=(byte)(msg[i*plen+j+off]^password[j]);
                }
            }
            for (int i=0;i<(mlen%plen);i++){
                msg[(mlen/plen)*plen+i+off]=(byte)(msg[(mlen/plen)*plen+i+off]^password[i]);
            }
        }else {
            for (int i=0;i<mlen;i++){
                msg[i+off]=(byte) (msg[i+off]^password[i]);
            }
        }
    }

    public static void xor(byte[] password,ByteBuf frame,int off,int mlen){
        int plen = password.length;
        if (mlen>plen){
            for (int i=0;i<(mlen/plen);i++){
                for (int j=0;j<plen;j++){
                    frame.setByte(i*plen+j+off,frame.getByte(i*plen+j+off)^password[j]);
                }
            }
            for (int i=0;i<(mlen%plen);i++){
                frame.setByte((mlen/plen)*plen+i+off,frame.getByte((mlen/plen)*plen+i+off)^password[i]);
            }
        }else {
            for (int i=0;i<mlen;i++){
                frame.setByte(i+off,frame.getByte(i+off)^password[i]);
            }
        }
    }

    public static short checkSum(ByteBuf frame,int mlen){
        int sum =0;
        for (int i=0;i<mlen;i++){
            sum+=(frame.getByte(i)&0xff);
        }
        return (short)(sum&0xffff);
    }
}
