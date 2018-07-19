package com.netflix.hystrix.dashboard.data.app.observer;

import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;

import java.util.*;

/**
 *
 * 被观察者
 * 主要是app的添加和删除触发相关动作
 * @author zhou
 * Created on 2018/7/17
 */
public class AppObservable extends Observable {

    private static AppObservable INSTANCE = new AppObservable();

    public static AppObservable getInstance(){
        return INSTANCE;
    }
    private AppObservable(){

    }

    private Vector<EurekaAppInfo> eurekaAppInfoVector= new Vector<>();


    public  synchronized void addAppAndNotify(EurekaAppInfo eurekaAppInfo) {
        if (null == eurekaAppInfo) {
//            throw new NullPointerException("addAppAndNotify eurekaAppInfo 不能为空！");
            return;
        }
        if (!eurekaAppInfoVector.contains(eurekaAppInfo)) {
            eurekaAppInfoVector.add(eurekaAppInfo);
            setChanged();//protected方法 只能继承后调用
            notifyObservers(new NotifyMessage(MessageTypeEnum.APP_ADDED,eurekaAppInfo));
        }
    }


    public synchronized void  removeAppAndNotify(EurekaAppInfo eurekaAppInfo) {
        if (null == eurekaAppInfo) {
//            throw new NullPointerException("removeAppAndNotify eurekaAppInfo 不能为空！");
            return;
        }
        if (eurekaAppInfoVector.contains(eurekaAppInfo)) {
            eurekaAppInfoVector.remove(eurekaAppInfo);
            setChanged();//protected方法 只能继承后调用
            notifyObservers(new NotifyMessage(MessageTypeEnum.APP_REMOVED,eurekaAppInfo));
        }
    }


    public boolean containsApp(EurekaAppInfo arg)
    {
        return eurekaAppInfoVector.contains(arg);
    }

}
