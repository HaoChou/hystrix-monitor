package com.netflix.hystrix.dashboard.data.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.stereotype.Component;

/**
 * @author zhou
 * Created on 2018/7/13
 */
public class LocalServer {
    private String localAddress;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workGroup;

    public LocalServer(String localAddress) {
        this.bossGroup = new LocalEventLoopGroup(1);
        this.workGroup = new LocalEventLoopGroup();
        this.localAddress = localAddress;
    }

    public void start() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new LocalEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workGroup);
            b.channel(LocalServerChannel.class);
            b.childHandler(new ChannelInitializer<LocalChannel>() {
                @Override
                protected void initChannel(LocalChannel ch) throws Exception {
                    ch.pipeline().addLast(new LocalServerHandler());
                }
            });
            LocalAddress address = new LocalAddress(this.localAddress);
            ChannelFuture future = b.bind(address).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("local server successively bind");
                }
            });
//            future.channel().closeFuture().sync();
        } catch (Exception e) {
            System.out.println("error !" + e);
        }
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
