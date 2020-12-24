package com.edgeMapper.EdgeMapper.netty;//package com.edgeMapper.EdgeMapper.netty;
//
//import com.edgeMapper.EdgeMapper.service.DeviceDataService;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
///**
// * Created by huqiaoqian on 2020/10/8
// */
//@Slf4j
//@Component
//@ChannelHandler.Sharable
//public class NettyServerHandler extends ChannelInboundHandlerAdapter {
//    private static NettyServerHandler nettyServerHandler;
//    @Autowired
//    DeviceDataService dataService;
//    @PostConstruct
//    public void init() {
//        nettyServerHandler = this;
//    }
//
//    @Override
//    @SneakyThrows
//    public void channelActive(ChannelHandlerContext context) {
//        log.debug("Listener({}) accept clint({})", context.channel().localAddress(), context.channel().remoteAddress());
//    }
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        byte[] data=(byte[])msg;
//        System.out.println("数据长度是："+data.length);
//        System.out.println("接收到的数据是："+data.toString());
//
//    }
//}
