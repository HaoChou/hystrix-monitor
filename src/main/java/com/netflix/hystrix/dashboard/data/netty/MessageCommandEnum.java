package com.netflix.hystrix.dashboard.data.netty;

/**
 * @author zhou
 * Created on 2018/7/20
 */
public enum MessageCommandEnum {
    DATA("DATA","数据"),
    REG_LOCAL("REG_LOCAL","本地client注册"),
    REG_PROXY("REG_PROXY","代理client注册");

    private String command;
    private String desc;

    MessageCommandEnum(String command, String desc) {
        this.command = command;
        this.desc = desc;
    }
}
