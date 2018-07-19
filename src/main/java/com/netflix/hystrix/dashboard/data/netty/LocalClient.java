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
 * 本地的netty客户端
 * @author zhou
 * Created on 2018/7/17
 */
public class LocalClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalClient.class);

    private final String name;
    private final String remoteAddress;
    private final EventLoopGroup eventLoopGroup ;

    private final EurekaAppInfo eurekaAppInfo;

    public LocalClient(String name,String remoteAddress, EurekaAppInfo eurekaAppInfo) {
        this.name=name;
        this.remoteAddress = remoteAddress;
        this.eurekaAppInfo = eurekaAppInfo;
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
                            .addLast(new ClientHandler(LocalClient.this));

                }
            });
            LocalAddress address = new LocalAddress(this.remoteAddress);
            ChannelFuture future = b.connect(address).sync();
            future.channel().closeFuture().sync();
            LOGGER.info("LocalClient与服务端断开！："+eurekaAppInfo.getHystrixStreamUrl());
        } catch (Exception e) {
            LOGGER.error("LocalClient异常：",e);
        }
    }

    public String getUrl() {
        return eurekaAppInfo.getHystrixStreamUrl();
    }

    public String getName(){
        return name;
    }


    public void shutdown() {
        eventLoopGroup.shutdownGracefully();
    }

}
