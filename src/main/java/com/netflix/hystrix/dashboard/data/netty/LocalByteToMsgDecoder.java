package com.netflix.hystrix.dashboard.data.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 服务端解码
 * @author zhou
 * Created on 2018/7/13
 */
public class LocalByteToMsgDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        ByteBuf byteBuf = in.readBytes(in.readableBytes());
        out.add(new String(byteBuf.array()));
    }
}
