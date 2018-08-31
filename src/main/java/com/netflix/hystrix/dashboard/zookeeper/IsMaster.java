package com.netflix.hystrix.dashboard.zookeeper;

/**
 * @author zhou
 * Created on 2018/7/25
 */
public interface IsMaster {

    boolean isMaster();
    void initAndCheck();
}
