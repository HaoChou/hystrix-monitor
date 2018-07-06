package com.netflix.hystrix.dashboard.config;

import com.netflix.hystrix.dashboard.stream.EurekaInfoServlet;
import com.netflix.hystrix.dashboard.stream.MockStreamServlet;
import com.netflix.hystrix.dashboard.stream.ProxyStreamServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhou
 * Created on 2018/7/6
 */
@Configuration
public class ServletConfig
{


	@Bean
	public ServletRegistrationBean proxyStreamServlet() {
		return new ServletRegistrationBean(new ProxyStreamServlet(), "/proxy.stream");
	}

	@Bean
	public ServletRegistrationBean mockStreamServlet() {
		return new ServletRegistrationBean(new MockStreamServlet(), "/mock.stream");
	}

	@Bean
	public ServletRegistrationBean eurekaInfoServlet() {
		return new ServletRegistrationBean(new EurekaInfoServlet(), "/eureka");
	}

}
