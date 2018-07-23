package com.netflix.hystrix.dashboard.data.netty.protobuf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhou
 * Created on 2018/7/23
 */
public class MessageTypeManager {
    private static final MessageTypeManager INSTACE = new MessageTypeManager();

    private MessageTypeManager(){

    }

    private static final Map<Short,String> commandId2ClassName = new HashMap<>();
    private static final Map<String,Short> className2CommandId = new HashMap<>();

    static {

        //id 2 class
        commandId2ClassName.put(MessageType.CHANNEL_REGISTER, Message.Register.class.getName());
        commandId2ClassName.put(MessageType.NORMAL_MESSAGE, Message.NormalMessage.class.getName());

        //calss 2 id
        for (Map.Entry<Short,String> entry :commandId2ClassName.entrySet()){
            className2CommandId.put(entry.getValue(),entry.getKey());
        }
    }


    public static MessageTypeManager getInstance() {
        return INSTACE;
    }

    public String getClassNameByCommandId(Short commandId){
        return commandId2ClassName.get(commandId);
    }

    public Short getCommandIdbyClassname(String className){
        return className2CommandId.get(className);
    }

}
