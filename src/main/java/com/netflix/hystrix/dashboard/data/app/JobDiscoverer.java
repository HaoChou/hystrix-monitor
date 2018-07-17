package com.netflix.hystrix.dashboard.data.app;

/**
 * 这里指的任务（app）是指 从微服务获取hystrix.stream的任务
 * @author zhou
 * Created on 2018/7/14
 */
public interface JobDiscoverer {

    /**
     * 是否有权利进行job发现
     * 如果在一个集群中 应该只有一个发现者 由他进行
     * @return
     */
    boolean hasPrivilege();

    void doDiscoverer();

    void setJobDiscoverHandler(JobDiscoverHandler jobDiscoverHandler);

}
