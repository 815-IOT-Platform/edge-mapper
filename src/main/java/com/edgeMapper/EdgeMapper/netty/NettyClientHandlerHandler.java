package com.edgeMapper.EdgeMapper.netty;//package com.edgeMapper.EdgeMapper.netty;
//
//import com.edgeMapper.EdgeMapper.service.DeviceDataService;
//import com.edgeMapper.EdgeMapper.service.RuleService;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
///**
// * Created by huqiaoqian on 2020/11/4
// */
//@Slf4j
//@Component
//@ChannelHandler.Sharable
//public class NettyClientHandlerHandler extends SimpleChannelInboundHandler<ByteBuf> {
//
//    @Autowired
//    private DeviceDataService deviceDataService;
//
//    @Autowired
//    private RuleService ruleService;
//
//    @Autowired
//    private NettyClient zigbeeClient;
//
//    @Override
//    public void channelActive (ChannelHandlerContext ctx) {
//        log.info("连接成功！");
//    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
//        byte[] bytes = new byte[byteBuf.readableBytes()];
//        int readerIndex = byteBuf.readerIndex();
//        byteBuf.getBytes(readerIndex, bytes);
//        deviceDataService.processMsg(bytes);
//        ruleService.process(bytes);
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("Disconnected with the remote client.");
//        zigbeeClient.reconnect();
//
//        // do something
//        super.channelInactive(ctx);
//    }
//
//    @Override
//    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) {
//        log.info("连接断开");
//        cause.printStackTrace();
//        ctx.channel().close();
//    }
//}
