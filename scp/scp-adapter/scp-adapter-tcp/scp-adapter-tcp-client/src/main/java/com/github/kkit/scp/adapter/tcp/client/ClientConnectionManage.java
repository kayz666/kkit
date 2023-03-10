//package com.github.kkit.scp.adapter.tcp.client;
//
//import com.github.kkit.scp.adapter.tcp.codec.ProtocolMessage;
//import com.github.kkit.scp.core.ChannelEvent;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.*;
//import io.netty.handler.timeout.IdleState;
//import io.netty.handler.timeout.IdleStateEvent;
//import io.netty.util.Timeout;
//import io.netty.util.Timer;
//import io.netty.util.TimerTask;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import static java.util.concurrent.TimeUnit.MILLISECONDS;
//
//@RequiredArgsConstructor
//@ChannelHandler.Sharable
//public abstract class ClientConnectionManage extends ChannelInboundHandlerAdapter implements TimerTask {
//
//    private static final Logger log = LoggerFactory.getLogger("SCP");
//
//    private final Bootstrap bootstrap;
//    private final Timer timer;
//
//    private final ChannelEvent channelEvent;
//    // 客户端连接
//    private Channel channel;
//
//    private int flag = 0;
//    // 是否重连标记
//    private volatile boolean reconnect = true;
//    // 重连尝试次数
//    private int attempts = 0;
//
//
//    //监听连接建立成功
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        if (log.isDebugEnabled()){
//            //log.debug("连接成功 本地地址:"+ctx.channel().localAddress().toString() + "  远程地址:"+ctx.channel().remoteAddress().toString());
//        }
//        channel = ctx.channel();
//        attempts = 0;
//        flag = 0;
//        channelEvent.channelOpen();
//    }
//
//    //监听连接断开
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        if (this.channel != null){
//
//        }
//        connectReconnect();
//        if (log.isDebugEnabled()){
//            //log.debug("连接断开 本地地址:"+ctx.channel().localAddress().toString() + "  远程地址:"+ scpConfig.getHost() + " port:" + scpConfig.getPort());
//        }
//        this.channel = null;
//        channelEvent.channelClose();
//    }
//
//
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        if (msg instanceof ProtocolMessage){
//            channelEvent.channelRead((ProtocolMessage)msg);
//        }
//    }
//
//
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
//        try {
//            channelEvent.exceptionCatch(cause);
//        }catch (Exception e){
//            log.error("未处理的链路异常 "+cause.getMessage(),cause);
//        }
//    }
//
//
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            IdleState state = ((IdleStateEvent) evt).state();
//        } else {
//            super.userEventTriggered(ctx, evt);
//        }
//    }
//
//
//    @Override
//    public void run(Timeout timeout) throws Exception {
//        this.flag = 2;
//        ChannelFuture future;
//        synchronized (bootstrap) {
//            bootstrap.handler(new ChannelInitializer<Channel>() {
//
//                @Override
//                protected void initChannel(Channel ch) throws Exception {
//                    ch.pipeline().addLast(handlers());
//                }
//            });
//            future = bootstrap.connect(scpConfig.getHost(),scpConfig.getPort());
//        }
//
//        future.addListener(new ChannelFutureListener() {
//
//            public void operationComplete(ChannelFuture f) throws Exception {
//                boolean succeed = f.isSuccess();
//                // Log.d("连接结果 ","" + succeed);
//                if (!succeed) {
//                    //f.channel().pipeline().fireChannelInactive();
//                    connectReconnect();
//                    ClientConnectionManage.this.flag = 3;
//                } else {
//                    ClientConnectionManage.this.flag = 4;
//                }
//            }
//        });
//    }
//
//    private void connectReconnect(){
//        if (reconnect) {
//            this.flag = 1;
//            long timeout = 2 << attempts;
//            if (attempts == 0) {
//
//            }
//            log.info("连接断开," + timeout + "ms后开始尝试重连");
//            if (attempts < 11) {
//                attempts++;
//            }
//            timer.newTimeout(this, timeout, MILLISECONDS);
//        }
//    }
//
//
//
//}
