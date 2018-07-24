#熔断器监控平台

主要特性
* 监控单台机器的熔断器使用情况
* 监控整个集群的熔断器使用情况（需要开发 集成turbine）



 监控熔断器的使用情况，可以帮助我们找出最接近每个微服务最优的配置参数，节约系统资源，提高吞吐，防止被"雪崩"的请求击穿！
 
 可以参考 https://github.com/Netflix/Hystrix/wiki/Metrics-and-Monitoring
 
 HystrixThreadPool的字段
* __name：线程池名称__
* __currentTime：当前时间__
* currentActiveCount：当前活动线程
* currentCompletedTaskCount：当前完成的任务数
* currentCorePoolSize：当前线程池线poolSize
* currentLargestPoolSize：当前线程池最大线程数
* currentMaximumPoolSize：
* currentPoolSize：
* currentQueueSize：
* currentTaskCount：
* rollingCountThreadsExecuted：
* rollingMaxActiveThreads：最大激活线程数
* rollingCountCommandRejections：线程池拒绝数
* propertyValue_queueSizeRejectionThreshold：5
* propertyValue_metricsRollingStatisticalWindowInMilliseconds：
* reportingHosts

HystrixCommand的字段
* __name：command名称__
* __group：属于哪个group的__
* __currentTime 当前时间__
* __isCircuitBreakerOpen：熔断器是否打开__
* __errorPercentage：错误百分比__
* __errorCount：错误数量__
* __requestCount：请求数量__
* __rollingCountBadRequests：统计窗口内的错误请求数__
* rollingCountCollapsedRequests：统计窗口内的合并请求数据
* rollingCountEmit：
* __rollingCountExceptionsThrown：统计窗口内异常抛出数__
* __rollingCountFailure:失败数__
* rollingCountEmit
* __rollingCountFallbackFailure:fallback失败__
* __rollingCountFallbackRejection:fallback拒绝__
* __rollingCountFallbackSuccess__:fallback执行成功的
* __rollingCountResponsesFromCache:走缓存的数__
* rollingCountSemaphoreRejected
* __rollingCountShortCircuited__
* __rollingCountSuccess：窗口成功的请求量__
* __rollingCountThreadPoolRejected：窗口内线程池拒绝数__
* __rollingCountTimeout：超时的__
* __currentConcurrentExecutionCount：正在执行的数__
* __rollingMaxConcurrentExecutionCount：__
* __latencyExecute_mean：计算的是 HystrixCommand.run()的时间__
* latencyExecute:{"0":1,"25":2,"50":3,"75":3,"90":4,"95":5,"99":5,"99.5":5,"100":5}
* __latencyTotal_mean: 计算的是HystrixCommand.execute()到返回到时间__
* latencyTotal:{"0":1,"25":3,"50":3,"75":3,"90":4,"95":5,"99":5,"99.5":5,"100":5}
* propertyValue_circuitBreakerRequestVolumeThreshold:
* propertyValue_circuitBreakerSleepWindowInMilliseconds:5000
* propertyValue_circuitBreakerErrorThresholdPercentage:
* propertyValue_circuitBreakerForceOpen:
* propertyValue_circuitBreakerForceClosed:
* propertyValue_circuitBreakerEnabled:
* propertyValue_executionIsolationStrategy:THREAD
* propertyValue_executionIsolationThreadTimeoutInMilliseconds:
* propertyValue_executionTimeoutInMilliseconds:
* propertyValue_executionIsolationThreadInterruptOnTimeout:
* propertyValue_executionIsolationThreadPoolKeyOverride:
* propertyValue_executionIsolationSemaphoreMaxConcurrentRequests:
* propertyValue_fallbackIsolationSemaphoreMaxConcurrentRequests:
* propertyValue_metricsRollingStatisticalWindowInMilliseconds:
* propertyValue_requestCacheEnabled:
* propertyValue_requestLogEnabled:
* reportingHosts:
* __threadPool:线程池名称__


latencyExecute_*计算的是 HystrixCommand.run()的时间

latencyTotal_*计算的时间是 HystrixCommand.execute() or HystrixCommand.queue() until a response is returned (or is ready to return in case of queue())
