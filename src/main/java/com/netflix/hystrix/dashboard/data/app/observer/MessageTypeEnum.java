package com.netflix.hystrix.dashboard.data.app.observer;

/**
 * @author zhou
 * Created on 2018/7/19
 */
public enum MessageTypeEnum {
    APP_ADDED("app被添加"),
    APP_REMOVED("app被删除");


    private String action;

    MessageTypeEnum(String action){
        this.action=action;
    }
}
