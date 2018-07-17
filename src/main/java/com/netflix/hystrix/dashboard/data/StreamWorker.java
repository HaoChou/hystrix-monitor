package com.netflix.hystrix.dashboard.data;

/**
 *
 * 从某一台服务器获取hystrix.stream的刘数据
 * @author zhou
 * Created on 2018/7/13
 */
public interface StreamWorker
{
	void init();

	void setConfig(WorkerConfig workerConfig);

	WorkerConfig getConfig();

	void doWork();
}
