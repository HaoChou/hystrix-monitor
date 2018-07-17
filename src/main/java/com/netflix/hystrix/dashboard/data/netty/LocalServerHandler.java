package com.netflix.hystrix.dashboard.data.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author zhou
 * Created on 2018/7/13
 */
public class LocalServerHandler extends SimpleChannelInboundHandler<String>  {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//        ctx.write("server has handler the msg:" + msg);
//        ctx.flush();
        System.out.println("服务端收到:" +msg);
    }
}
