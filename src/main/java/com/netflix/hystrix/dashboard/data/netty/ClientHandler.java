package com.netflix.hystrix.dashboard.data.netty;

import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import com.netflix.hystrix.dashboard.data.netty.thread.StreamClientRunnable;
import com.netflix.hystrix.dashboard.threadpool.LocalThreadPoolManger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zhou
 * Created on 2018/7/13
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleChannelInboundHandler.class);

    private final LocalClient localClient;
    private final EurekaAppInfo eurekaAppInfo;

    public ClientHandler(LocalClient localClient) {
        this.localClient = localClient;
        eurekaAppInfo=localClient.getEurekaAppInfo();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("client [" + eurekaAppInfo.toString()+ "] connected");
//        ctx.writeAndFlush("client [" + eurekaAppInfo.toString() + "] connected");
        ctx.writeAndFlush("local|"+eurekaAppInfo.getHystrixStreamUrl());
        LocalThreadPoolManger.getInstance().getBizThreadPool().execute(new StreamClientRunnable(eurekaAppInfo,ctx.channel()));
        LOGGER.info("client 任务已经提交" +eurekaAppInfo.toString());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("client received : " + msg);
    }


}