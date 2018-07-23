package com.netflix.hystrix.dashboard.data.netty;

import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import com.netflix.hystrix.dashboard.data.netty.codec.Int32FrameDecoder;
import com.netflix.hystrix.dashboard.data.netty.codec.Int32FrameEncoder;
import com.netflix.hystrix.dashboard.data.netty.codec.LocalMessageDecoder;
import com.netflix.hystrix.dashboard.data.netty.codec.LocalMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本地的netty客户端
 *
 * @author zhou
 * Created on 2018/7/17
 */
public class LocalClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalClient.class);

    private final String name;
    private final String remoteAddress;
    private final EventLoopGroup eventLoopGroup;

    private final EurekaAppInfo eurekaAppInfo;

    public LocalClient(String name, String remoteAddress, EurekaAppInfo eurekaAppInfo) {
        this.name = name;
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
                            .addLast(new Int32FrameDecoder())
                            .addLast(new LocalMessageDecoder())
                            .addLast(new ClientHandler(LocalClient.this))
                            .addLast(new LocalMessageEncoder())
                            .addLast(new Int32FrameEncoder());


                }
            });
            LocalAddress address = new LocalAddress(this.remoteAddress);
            ChannelFuture future = b.connect(address).sync();
            future.channel().closeFuture().sync();
            LOGGER.info("LocalClient与服务端断开！：" + eurekaAppInfo.getHystrixStreamUrl());
        } catch (Exception e) {
            LOGGER.error("LocalClient异常：", e);
        }
    }

    public EurekaAppInfo getEurekaAppInfo() {
        return eurekaAppInfo;
    }

    public String getName() {
        return name;
    }


    public void shutdown() {
        LOGGER.info("clien:" + eurekaAppInfo.toString() + "断开");
        eventLoopGroup.shutdownGracefully();
    }

}
