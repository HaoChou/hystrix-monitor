package com.netflix.hystrix.dashboard.data.netty.codec;

import com.google.protobuf.GeneratedMessage;
import com.netflix.hystrix.dashboard.data.netty.protobuf.MessageTypeManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author zhou
 * Created on 2018/7/23
 */
public class LocalMessageEncoder extends MessageToByteEncoder<GeneratedMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, GeneratedMessage message, ByteBuf out) throws Exception {
        String className = message.getClass().getName();
        short methodId = MessageTypeManager.getInstance().getCommandIdbyClassname(className);
        out.writeShort(methodId);
        out.writeBytes(message.toByteArray());
    }
}
