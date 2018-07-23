package com.netflix.hystrix.dashboard.data.netty.protobuf;

/**
 * @author zhou
 * Created on 2018/7/23
 */
public class MessageType {


    public static final short CHANNEL_REGISTER = 0x0000;  //注册命令
    public static final short NORMAL_MESSAGE = 0x0001; //hystrix_stream数据

}
