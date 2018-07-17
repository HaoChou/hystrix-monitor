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

    private Set<EurekaAppInfo> eurekaAppInfoSet = new HashSet<>();


    public  void addAppAndNotify(EurekaAppInfo eurekaAppInfo){
        synchronized (this) {
            if (null == eurekaAppInfo) {
                throw new NullPointerException("addAppAndNotify eurekaAppInfo 不能为空！");
            }
            if (!eurekaAppInfoSet.contains(eurekaAppInfo)) {
                eurekaAppInfoSet.add(eurekaAppInfo);
                setChanged();//protected方法 只能继承后调用
                notifyObservers(eurekaAppInfo);
            }
        }
    }


    public void removeAppAndNotify(EurekaAppInfo eurekaAppInfo){
        synchronized (this) {
            if (null == eurekaAppInfo) {
                throw new NullPointerException("removeAppAndNotify eurekaAppInfo 不能为空！");
            }
            if (!eurekaAppInfoSet.contains(eurekaAppInfo)) {
                eurekaAppInfoSet.remove(eurekaAppInfo);
                setChanged();//protected方法 只能继承后调用
                notifyObservers(eurekaAppInfo);
            }
        }
    }

    public boolean containsApp(EurekaAppInfo arg)
    {
        return eurekaAppInfoSet.contains(arg);
    }

}
