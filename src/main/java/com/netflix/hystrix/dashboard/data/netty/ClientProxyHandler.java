package com.netflix.hystrix.dashboard.data.netty;

import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import com.netflix.hystrix.dashboard.data.app.LocalEurekaAppRegister;
import com.netflix.hystrix.dashboard.data.netty.thread.StreamClientRunnable;
import com.netflix.hystrix.dashboard.threadpool.LocalThreadPoolManger;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhou
 * Created on 2018/7/20
 */
public class ClientProxyHandler extends SimpleChannelInboundHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleChannelInboundHandler.class);

    private final LocalProxyClient localProxyClient;
    private final String hystrixUrl;

    public ClientProxyHandler(LocalProxyClient localProxyClient ,String hystrixUrl) {
        this.localProxyClient = localProxyClient;
        this.hystrixUrl=hystrixUrl;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("proxy client [" + hystrixUrl+ "] connected");
        ctx.writeAndFlush("proxy|"+hystrixUrl);
//        LOGGER.info("client 任务已经提交" +eurekaAppInfo.toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("proxy client received : " + msg);
    }
}
