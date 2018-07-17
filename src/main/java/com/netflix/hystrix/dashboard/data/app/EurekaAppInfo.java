package com.netflix.hystrix.dashboard.data.app;

/**
 * @author zhou
 * Created on 2018/7/17
 */
public class EurekaAppInfo {
    private String appName;
    private String ipAddr;
    private Integer port;
    private String uri;

    public EurekaAppInfo(String appName, String ipAddr, Integer port, String uri) {
        this.appName = appName;
        this.ipAddr = ipAddr;
        this.port = port;
        this.uri = uri;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHystrixStreamUrl(){
        return "http://"+ipAddr+":"+port+ uri;
    }
}
