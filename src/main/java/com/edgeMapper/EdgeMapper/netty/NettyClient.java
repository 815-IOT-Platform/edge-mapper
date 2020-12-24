package com.edgeMapper.EdgeMapper.netty;//package com.edgeMapper.EdgeMapper.netty;
//
//import com.edgeMapper.EdgeMapper.config.GatewayConfig;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.channel.socket.nio.NioSocketChannel;
//import io.netty.handler.timeout.ReadTimeoutHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.util.concurrent.TimeUnit;
//
///**
// * Created by huqiaoqian on 2020/11/4
// */
//@Slf4j
//@Component
//public class NettyClient {
//    @Autowired
//    private NettyClientHandlerHandler nettyClientHandlerHandler;
//
//    @Autowired
//    private GatewayConfig gatewayConfig;
//
//    private EventLoopGroup group = new NioEventLoopGroup();
//
//    private volatile Channel channel;
//
//    @PostConstruct
//    public void init() throws InterruptedException {
//        Bootstrap b = new Bootstrap();
//        b.group(group)
//                .channel(NioSocketChannel.class)
//                .option(ChannelOption.SO_KEEPALIVE,true)
//                .remoteAddress(gatewayConfig.getHost(),gatewayConfig.getPort())
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    public void initChannel (SocketChannel ch)
//                            throws Exception {
//                        ch.pipeline()
//                                .addLast(new ReadTimeoutHandler(5))
//                                .addLast(
//                                        nettyClientHandlerHandler
//                                );
//
//                    }
//                });
//        b.connect().addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                if (!channelFuture.isSuccess()) {
//                    log.error("[start][Netty Client 连接服务器({}:{}) 失败]", gatewayConfig.getHost(), gatewayConfig.getPort());
//                    reconnect();
//                    return;
//                }
//
//                channel = channelFuture.channel();
//                log.error("[start][Netty Client 连接服务器({}:{}) 成功]", gatewayConfig.getHost(), gatewayConfig.getPort());
//            }
//        });
//    }
//
//    public void reconnect() {
//        group.schedule(new Runnable() {
//            @Override
//            public void run() {
//                log.info("[reconnect]开始重连");
//                try {
//                    init();
//                } catch (InterruptedException e) {
//                    log.error("[reconnect][重连失败]", e);
//                }
//            }
//        },5, TimeUnit.SECONDS);
//        log.info("[reconnect][{} 秒后将发起重连]",5);
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        if (channel != null) {
//            channel.close();
//        }
//        group.shutdownGracefully();
//    }
//}
