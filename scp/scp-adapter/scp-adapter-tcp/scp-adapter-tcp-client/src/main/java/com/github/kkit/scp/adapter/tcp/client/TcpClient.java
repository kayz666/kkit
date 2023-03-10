//package com.github.kkit.scp.adapter.tcp.client;
//
//import com.github.kkit.scp.adapter.tcp.codec.ProtocolMessageDecoder;
//import com.github.kkit.scp.adapter.tcp.codec.ProtocolMessageEncoder;
//import com.github.kkit.scp.common.NativeSupport;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.buffer.ByteBufAllocator;
//import io.netty.channel.*;
//import io.netty.channel.epoll.EpollEventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.concurrent.ExecutorService;
//
//import static java.util.concurrent.TimeUnit.SECONDS;
//
//public class TcpClient {
//
//    private static final Logger log = LoggerFactory.getLogger("SCP");
//    public static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
//
//    private Bootstrap bootstrap;
//    private EventLoopGroup worker;
//
//    private ExecutorService workerFactory;
//    protected volatile ByteBufAllocator allocator;
//    private String serverHost;
//    private int serverPort;
//    private byte[] connectPassword;
//    private int maxMessageLength = 20971520;  // 20M
//
//    private ClientConnectionManage clientConnectionManage;
//    private TcpClient (){
//        init();
//    }
//
//
//    public static TcpClient getInstance(){
//        return new TcpClient();
//    }
//
//    public void init(){
//        int nWorkers = AVAILABLE_PROCESSORS << 1;
//        worker = NativeSupport.isSupportNativeET() ? new EpollEventLoopGroup(nWorkers) : new NioEventLoopGroup(nWorkers);
//        bootstrap = new Bootstrap().group(worker);
//        bootstrap
//                .option(ChannelOption.ALLOCATOR,allocator)
//                .option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, DefaultMessageSizeEstimator.DEFAULT)
//                .option(ChannelOption.SO_REUSEADDR,true)
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,(int) SECONDS.toMillis(3))
//                .channel(NioSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE,true)
//                .option(ChannelOption.TCP_NODELAY,true)
//                .option(ChannelOption.ALLOW_HALF_CLOSURE,false);
//        initCodec();
//    }
//
//
//    public void initCodec(){
//        bootstrap.handler(new ChannelInitializer<Channel>() {
//            @Override
//            protected void initChannel(Channel ch) throws Exception {
//                ch.pipeline().addLast(
//                        new ProtocolMessageEncoder(connectPassword),
//                        new ProtocolMessageDecoder(maxMessageLength,connectPassword),
//                        clientConnectionManage
//                );
//            }
//        });
//    }
//
//    public void connect(){
//        ChannelFuture future;
//        synchronized (bootstrap) {
//            future = bootstrap.connect(serverHost,serverPort);
//        }
//
//        future.addListener(new ChannelFutureListener() {
//
//            public void operationComplete(ChannelFuture f) throws Exception {
//                boolean succeed = f.isSuccess();
//                // Log.d("连接结果 ","" + succeed);
//                if (!succeed) {
//                    //time = System.currentTimeMillis();
//                    //f.channel().pipeline().fireChannelInactive();
//                    //connectReconnect();
//
//                } else {
//
//                }
//            }
//        });
//    }
//
//
//    public void disconnect() {
//        worker.shutdownGracefully();
//        while (!worker.isShutdown()){
//            try {
//                Thread.sleep(10);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
