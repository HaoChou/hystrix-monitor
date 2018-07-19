package com.netflix.hystrix.dashboard.data.app.observer;

/**
 * @author zhou
 * Created on 2018/7/19
 */
public class NotifyMessage {

    private final   MessageTypeEnum typeEnum;
    private final Object arg;

    public NotifyMessage(MessageTypeEnum typeEnum,Object arg){
        if(null==typeEnum||null==arg){
            throw new NullPointerException("NotifyMessage 构造函数参数不能为空");
        }
        this.typeEnum=typeEnum;
        this.arg=arg;
    }

    public MessageTypeEnum getTypeEnum() {
        return typeEnum;
    }

    public Object getArg() {
        return arg;
    }

}
