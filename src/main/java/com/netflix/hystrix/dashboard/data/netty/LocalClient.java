package com.netflix.hystrix.dashboard.data.netty;

import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;

/**
 * 本地的netty客户端
 * @author zhou
 * Created on 2018/7/17
 */
public class LocalClient {

    private final String remoteAddress;
    private final EventLoopGroup eventLoopGroup ;

    private final EurekaAppInfo eurekaAppInfo;

    public LocalClient(String remoteAddress, EurekaAppInfo eurekaAppInfo) {
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
        } catch (Exception e) {
            System.out.println("error !" + e);
        }
    }

    public String getUrl() {
        return eurekaAppInfo.getHystrixStreamUrl();
    }


}
