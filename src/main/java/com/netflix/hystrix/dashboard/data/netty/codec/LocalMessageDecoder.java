package com.netflix.hystrix.dashboard.data.netty.codec;

import com.google.protobuf.GeneratedMessage;
import com.netflix.hystrix.dashboard.data.netty.protobuf.MessageTypeManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhou
 * Created on 2018/7/23
 */
public class LocalMessageDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalMessageDecoder.class);

    private static ConcurrentHashMap<String, Class<? extends GeneratedMessage>> name2classMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Method> name2methodMap = new ConcurrentHashMap<>();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() > 2) {
            short methodId = in.readShort();
            byte[] message = new byte[in.readableBytes()];
            in.readBytes(message);
            out.add(decode(methodId, message));
        }
    }



    @SuppressWarnings("unchecked")
    public static GeneratedMessage decode(short methodId ,byte[] message) {

        String classNameStr = MessageTypeManager.getInstance().getClassNameByCommandId(methodId);
        Class<? extends GeneratedMessage> generatedMessageClass = name2classMap.get(classNameStr);
        Method parseMethod = name2methodMap.get(classNameStr);
        try {

            if (generatedMessageClass == null) {
                generatedMessageClass = (Class<? extends GeneratedMessage>) Class.forName(classNameStr);
                parseMethod = generatedMessageClass.getDeclaredMethod("parseFrom", byte[].class);
                name2classMap.put(classNameStr, generatedMessageClass);
                name2methodMap.put(classNameStr, parseMethod);
            }

            GeneratedMessage generatedMessage = (GeneratedMessage) parseMethod.invoke(generatedMessageClass, message);
            return generatedMessage;
        } catch (Exception e) {
            LOGGER.warn("deocde message error.", e);
            return null;
        }
    }
}
