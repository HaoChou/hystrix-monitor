package com.netflix.hystrix.dashboard.data.netty;

import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本地代理 返回某一个app的stream
 * 多个请求同一个app的共用一个
 * @author zhou
 * Created on 2018/7/20
 */
public class LocalProxyClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalProxyClient.class);

    private final String name;
    private final String remoteAddress;
    private final EventLoopGroup eventLoopGroup ;

    private final String hystrixStreamUrl;

    public LocalProxyClient(String name,String remoteAddress, String hystrixStreamUrl) {
        this.name=name;
        this.remoteAddress = remoteAddress;
        this.hystrixStreamUrl = hystrixStreamUrl;
        this.eventLoopGroup = new LocalEventLoopGroup();
    }

    public void start() {
        try {
            Bootstrap b = new Bootstrap();
            b.group(eventLoopGroup);
            b.channel(LocalChannel.class);
            b.handler(new ChannelInitializer<LocalChannel>() {
                @Override
                protected void initChannel(LocalChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new ClientProxyHandler(LocalProxyClient.this,hystrixStreamUrl));

                }
            });
            LocalAddress address = new LocalAddress(this.remoteAddress);
            ChannelFuture future = b.connect(address).sync();
            future.channel().closeFuture().sync();
            LOGGER.info("ProxyLocalClient与服务端断开！："+hystrixStreamUrl);
        } catch (Exception e) {
            LOGGER.error("ProxyLocalClient ：",e);
        }
    }


    public String getName(){
        return name;
    }


    public void shutdown() {
        LOGGER.info("ProxyLocalClient:"+hystrixStreamUrl.toString()+"断开");
        eventLoopGroup.shutdownGracefully();
    }
}
