package com.netflix.hystrix.dashboard.data.app;

import java.util.Observable;
import java.util.Observer;

/**
 *  hystrix流的观察者 负责开启流的读取任务和删除任务
 * @author zhou
 * Created on 2018/7/17
 */
public class HystrixStreamAppObserver implements Observer {
    @Override
    public void update(Observable o, Object arg) {

        if(o instanceof AppObservable&& arg instanceof EurekaAppInfo) {
            AppObservable observable = (AppObservable) o;
            EurekaAppInfo eurekaAppInfo = (EurekaAppInfo) arg;
            if(observable.containsApp(eurekaAppInfo)){
                //add
                String hystrixStreamUrl = eurekaAppInfo.getHystrixStreamUrl();
            }
            else {
                //remove
            }
        }
    }
}
