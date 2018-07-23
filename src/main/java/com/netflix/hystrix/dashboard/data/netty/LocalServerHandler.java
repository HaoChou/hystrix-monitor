package com.netflix.hystrix.dashboard.data.netty;

import com.google.protobuf.GeneratedMessageV3;
import com.netflix.hystrix.dashboard.data.netty.protobuf.Message;
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


//        if(msg==null){
//            return;
//        }
//        String[] split = msg.split("\\|");
//        if(split.length==2){
//            if(split[0].equals("local")){
//
//                CLIENT_LOCAL_CLIENT.putIfAbsent(ctx.channel(),split[1]);
//                ctx.writeAndFlush("Local" + split[1] + "链接成功");
//            }
//            if(split[0].equals("proxy")) {
//                URL_PROXY_CLIENT.putIfAbsent(split[1], ctx.channel());
//                ctx.writeAndFlush("代理" + split[1] + "链接成功");
//            }
//        }
//
//
//        String url = CLIENT_LOCAL_CLIENT.get(ctx.channel());
//        Channel channel = URL_PROXY_CLIENT.get(url);
//        channel.writeAndFlush(msg);


        if(msg instanceof Message.Register){
            Message.Register registerCommand = (Message.Register) msg;
        }

        if(msg instanceof Message.NormalMessage){
            Message.NormalMessage message = (Message.NormalMessage) msg;
            System.out.println("收到消息："+message.getContent());
        }
    }
}
