package com.netflix.hystrix.dashboard.data.app.observer;

import com.netflix.hystrix.dashboard.data.app.EurekaAppInfo;
import com.netflix.hystrix.dashboard.data.app.observer.AppObservable;

import java.util.Observable;
import java.util.Observer;

/**
 *  hystrix流的观察者 负责开启流的读取任务和删除任务
 *
 * @author zhou
 * Created on 2018/7/17
 */
public class HystrixStreamAppObserver implements Observer {
    @Override
    public void update(Observable o, Object arg) {

        if(o instanceof AppObservable && arg instanceof NotifyMessage) {
            AppObservable observable = (AppObservable) o;
            NotifyMessage notifyMessage = (NotifyMessage) arg;

            if(notifyMessage.getTypeEnum().equals(MessageTypeEnum.APP_ADDED)){
                if(notifyMessage.getArg() instanceof EurekaAppInfo){




                }
            }
            else if( notifyMessage.getTypeEnum().equals(MessageTypeEnum.APP_REMOVED) ){

            }

        }
    }
}
