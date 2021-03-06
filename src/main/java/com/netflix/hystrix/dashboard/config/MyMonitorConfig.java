package com.netflix.hystrix.dashboard.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author zhou
 * Created on 2018/7/17
 */
@Component
@ConfigurationProperties(prefix = "myMonitor")
public class MyMonitorConfig {
    private String eurekaAppsUrl;
    private String zkUrl;
    private Set<String> ignoreApps;


    public String getEurekaAppsUrl() {
        return eurekaAppsUrl;
    }

    public void setEurekaAppsUrl(String eurekaAppsUrl) {
        this.eurekaAppsUrl = eurekaAppsUrl;
    }

    public Set<String> getIgnoreApps() {
        return ignoreApps;
    }

    public void setIgnoreApps(Set<String> ignoreApps) {
        this.ignoreApps = ignoreApps;
    }


    public String getZkUrl() {
        return zkUrl;
    }

    public void setZkUrl(String zkUrl) {
        this.zkUrl = zkUrl;
    }
}
