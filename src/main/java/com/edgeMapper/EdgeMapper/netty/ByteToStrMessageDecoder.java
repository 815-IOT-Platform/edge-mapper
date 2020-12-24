package com.edgeMapper.EdgeMapper.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * Created by huqiaoqian on 2020/10/12
// */
public class ByteToStrMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in == null) return;
        //因为使用自定义协议解决粘包、拆包问题，自定义协议内部先传输数据长度，所以需在这里先接收长度
        if (in.readableBytes() <= 4) return;;
        in.markReaderIndex();
        int len = in.readInt();
        if (in.readableBytes() < len) {
            in.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[len];
        in.readBytes(bytes);
        String json = new String(bytes, CharsetUtil.UTF_8);
        in.markReaderIndex();
        //将处理后的数据，交由handler链中的下一个处理
        out.add(bytes);
    }
}
