package com.netflix.hystrix.dashboard.data.app;

import java.util.*;

/**
 *
 * 被观察者
 * 主要是app的添加和删除触发相关动作
 * @author zhou
 * Created on 2018/7/17
 */
public class AppObservable extends Observable {

    private Vector<EurekaAppInfo> eurekaAppInfoVector= new Vector<>();


    public  synchronized void addAppAndNotify(EurekaAppInfo eurekaAppInfo) {
        if (null == eurekaAppInfo) {
            throw new NullPointerException("addAppAndNotify eurekaAppInfo 不能为空！");
        }
        if (!eurekaAppInfoVector.contains(eurekaAppInfo)) {
            eurekaAppInfoVector.add(eurekaAppInfo);
            setChanged();//protected方法 只能继承后调用
            notifyObservers(eurekaAppInfo);
        }
    }


    public synchronized void  removeAppAndNotify(EurekaAppInfo eurekaAppInfo) {
        if (null == eurekaAppInfo) {
            throw new NullPointerException("removeAppAndNotify eurekaAppInfo 不能为空！");
        }
        if (!eurekaAppInfoVector.contains(eurekaAppInfo)) {
            eurekaAppInfoVector.remove(eurekaAppInfo);
            setChanged();//protected方法 只能继承后调用
            notifyObservers(eurekaAppInfo);
        }
    }


    public boolean containsApp(EurekaAppInfo arg)
    {
        return eurekaAppInfoVector.contains(arg);
    }

}
