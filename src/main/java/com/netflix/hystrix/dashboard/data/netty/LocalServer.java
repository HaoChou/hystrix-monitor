package com.netflix.hystrix.dashboard.data.netty;

import com.netflix.hystrix.dashboard.data.netty.codec.Int32FrameDecoder;
import com.netflix.hystrix.dashboard.data.netty.codec.Int32FrameEncoder;
import com.netflix.hystrix.dashboard.data.netty.codec.LocalMessageDecoder;
import com.netflix.hystrix.dashboard.data.netty.codec.LocalMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author zhou
 * Created on 2018/7/13
 */
public class LocalServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalServer.class);

    private String localAddress;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workGroup;

    public LocalServer(String localAddress) {
        this.bossGroup = new LocalEventLoopGroup(1);
        this.workGroup = new LocalEventLoopGroup();
        this.localAddress = localAddress;
    }

    public void start() {
        EventLoopGroup eventLoopGroup = new LocalEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup);
            b.channel(LocalServerChannel.class);
            b.childHandler(new ChannelInitializer<LocalChannel>() {
                @Override
                protected void initChannel(LocalChannel ch) throws Exception {
                    ch.pipeline()
                            .addLast(new Int32FrameDecoder())
                            .addLast(new LocalMessageDecoder())
                            .addLast(new LocalServerHandler())
                            .addLast(new LocalMessageEncoder())
                            .addLast(new Int32FrameEncoder());
                }
            });
            LocalAddress address = new LocalAddress(this.localAddress);
            ChannelFuture future = b.bind(address).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("LocalServer成功绑定：" + localAddress);
                }
            });
            future.channel().closeFuture().sync();
            LOGGER.info("LocalServer与服务端断开！：");
        } catch (Exception e) {
            LOGGER.error("LocalServer异常：" + e);
        }
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
