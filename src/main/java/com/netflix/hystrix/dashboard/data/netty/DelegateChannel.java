package com.netflix.hystrix.dashboard.data.netty;


import io.netty.channel.Channel;

/**
 * @author zhou
 * Created on 2018/7/17
 */
public class DelegateChannel {

    private String hystrixUrl;//唯一id
    private Channel channel;

    public void writeAndFlush(String msg)
    {
        channel.writeAndFlush(msg);
    }

    public String getHystrixUrl() {
        return hystrixUrl;
    }

    public void setHystrixUrl(String hystrixUrl) {
        this.hystrixUrl = hystrixUrl;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
