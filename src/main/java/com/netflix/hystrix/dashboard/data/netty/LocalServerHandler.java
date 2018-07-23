package com.netflix.hystrix.dashboard.data.netty;

import com.google.protobuf.GeneratedMessageV3;
import com.netflix.hystrix.dashboard.data.netty.protobuf.Message;
import com.netflix.hystrix.dashboard.data.netty.thread.StreamDataWriteIntoDbRunnable;
import com.netflix.hystrix.dashboard.influxdb.LocalInfluxDB;
import com.netflix.hystrix.dashboard.threadpool.LocalThreadPoolManger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhou
 * Created on 2018/7/13
 */
public class LocalServerHandler extends SimpleChannelInboundHandler<GeneratedMessageV3>  {
    final private static Map<String,Channel> URL_PROXY_CLIENT = new ConcurrentHashMap();
    final private static Map<Channel,String> CLIENT_LOCAL_CLIENT = new ConcurrentHashMap();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GeneratedMessageV3 msg) throws Exception {



        if(msg instanceof Message.Register){
            Message.Register registerCommand = (Message.Register) msg;
            URL_PROXY_CLIENT.put(registerCommand.getAppInfo(),ctx.channel());
            CLIENT_LOCAL_CLIENT.put(ctx.channel(),registerCommand.getAppInfo());
//            System.out.println("收到注册消息"+registerCommand.getAppInfo());
        }else if(msg instanceof Message.NormalMessage){
            LocalThreadPoolManger.getInstance().getBizThreadPool().execute(new StreamDataWriteIntoDbRunnable(LocalInfluxDB.getInfluxDB(),(Message.NormalMessage)msg));
        }
    }
}
