package com.netflix.hystrix.dashboard.data;

import org.apache.commons.codec.binary.StringUtils;

/**
 * @author zhou
 * Created on 2018/7/13
 */
public interface WorkerConfig {
    String getIpAddreass();

    String getPort();

    String getUri();

    public static final class Default implements WorkerConfig {
        private final String ipAddress;
        private final String port;
        private final String uri;

        public Default(String ipAddress, String port, String uri) {
            if (ipAddress == null || ipAddress == ""
                    || port == null || port == ""
                    || uri == null || uri == "") {
                throw new NullPointerException("参数不能为空！");
            }
            this.ipAddress = ipAddress;
            this.port = port;
            this.uri = uri;
        }

        @Override
        public String getIpAddreass() {
            return ipAddress;
        }

        @Override
        public String getPort() {
            return port;
        }

        @Override
        public String getUri() {
            return uri;
        }
    }
}
