package com.example.demo1228_2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    //https://chatgpt.com/share/6716a676-be10-8004-bd6f-7dd1dd148d40
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8); // 核心线程数
        executor.setMaxPoolSize(900); // 最大线程数
        executor.setQueueCapacity(500); // 任务队列容量
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.setKeepAliveSeconds(60); // 空闲线程存活时间
        executor.initialize();

        // 设置拒绝策略为CallerRunsPolicy
        //executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        /*
        ThreadPoolExecutor.AbortPolicy（默认）：
        直接抛出RejectedExecutionException异常。
        这种策略在任务无法被处理时，会立即让调用者感知到任务被拒绝，通常用于需要及时发现和处理任务无法执行的情况。

        ThreadPoolExecutor.CallerRunsPolicy：
        由提交任务的线程执行该任务。
        如果线程池没有关闭，该策略会直接在调用execute方法的线程中运行被拒绝的任务。这可以避免任务的丢失，但可能会增加调用线程的负载。
        ThreadPoolExecutor.DiscardPolicy：
        忽略该任务，不会有任何反馈。
        这种策略会默默地丢弃无法处理的任务，不抛出异常也不进行任何记录。这通常用于那些可以容忍任务丢失的场景。

        ThreadPoolExecutor.DiscardOldestPolicy：
        丢弃队列中等待最久的任务，然后尝试提交当前任务。
        这种策略会丢弃队列中最旧的任务，以便为当前任务腾出空间。这通常用于那些希望优先处理新任务而可以牺牲一些旧任务的场景。
         */

        return executor;
    }
}

