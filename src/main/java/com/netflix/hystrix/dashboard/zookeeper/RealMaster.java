package com.netflix.hystrix.dashboard.zookeeper;

import com.alibaba.fastjson.JSON;
import com.netflix.hystrix.dashboard.config.MyMonitorConfig;
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
import java.util.ArrayList;
import java.util.List;


/**
 * @author zhou
 * Created on 2018/7/25
 */
@Component
public class RealMaster implements IsMaster{

    private static final Logger logger = LoggerFactory.getLogger(RealMaster.class);

    public static boolean isMaster = false;

    private static final String PATH = "/Hystrixonitor/leader";

    @Autowired
    private MyMonitorConfig myMonitorConfig;


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
        List<LeaderLatch> latchList = new ArrayList<>();
        List<CuratorFramework> clients = new ArrayList<>();
        try {
            CuratorFramework client = getClient();
            clients.add(client);

            final LeaderLatch leaderLatch = new LeaderLatch(client, PATH, "client#" + System.currentTimeMillis());
            leaderLatch.addListener(new LeaderLatchListener() {
                @Override
                public void isLeader() {
                    logger.info(leaderLatch.getId() +  ":I am leader. I am doing jobs!");
                    isMaster=true;

                }

                @Override
                public void notLeader() {
                    logger.info(leaderLatch.getId() +  ":I am not leader. I will do nothing!");
                    isMaster=false;
                }
            });
            latchList.add(leaderLatch);
            leaderLatch.start();

            System.out.println(JSON.toJSON(leaderLatch.getParticipants()));

            Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for(CuratorFramework client : clients){
                CloseableUtils.closeQuietly(client);
            }

            for(LeaderLatch leaderLatch : latchList){
                CloseableUtils.closeQuietly(leaderLatch);
            }
        }
    }
}
