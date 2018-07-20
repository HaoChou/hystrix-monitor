package com.netflix.hystrix.dashboard.data.netty;

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

    public ClientHandler(LocalClient localClient) {
        this.localClient = localClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.info("client [" + localClient.getUrl() + "] connected");
        ctx.writeAndFlush("client [" + localClient.getUrl() + "] connected");
        LocalThreadPoolManger.getInstance().getBizThreadPool().execute(new StreamClientRunnable(localClient.getUrl(),ctx.channel()));
        LOGGER.info("client 任务已经提交" +localClient.getUrl());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("client received : " + msg);
    }


}