package com.netflix.hystrix.dashboard.zookeeper;

import com.netflix.hystrix.dashboard.config.MyMonitorConfig;
import com.netflix.hystrix.dashboard.data.app.JobDiscoverHandler;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


/**
 * @author zhou
 * Created on 2018/7/25
 */
@Component
public class RealMaster implements IsMaster{

    private static final Logger logger = LoggerFactory.getLogger(RealMaster.class);

    public static boolean isMaster = false;

    private static final String PATH = "/Hystrixonitor/leader";

    private CuratorFramework client ;
    private LeaderLatch leaderLatch ;

    @Autowired
    private MyMonitorConfig myMonitorConfig;

    @Autowired
    JobDiscoverHandler jobDiscoverHandler;


    private CuratorFramework getClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(myMonitorConfig.getZkUrl())
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(6000)
                .connectionTimeoutMs(3000)
                .namespace("leaderRoot")
                .build();
        client.start();
        return client;
    }

    @Override
    public boolean isMaster() {
        return isMaster;
    }

    @PostConstruct
    @Override
    public void initAndCheck() {

        try {
            client = getClient();

            leaderLatch = new LeaderLatch(client, PATH, "client#" + System.currentTimeMillis());
            leaderLatch.addListener(new LeaderLatchListener() {
                @Override
                public void isLeader() {
                    logger.info(leaderLatch.getId() +  ":I am leader. I am doing jobs!");
                    isMaster=true;

                    jobDiscoverHandler.handler();

                }

                @Override
                public void notLeader() {
                    logger.info(leaderLatch.getId() +  ":I am not leader. I will do nothing!");
                    isMaster=false;
                }
            });
            leaderLatch.start();
            logger.info("isMaster:"+isMaster);

        } catch (Exception e) {
            logger.error("选举异常",e);
        }
    }

    @PreDestroy
    private void distory(){
        if (null!=client){
            CloseableUtils.closeQuietly(client);
        }
        if (null!=leaderLatch){
            CloseableUtils.closeQuietly(leaderLatch);
        }
    }
}
