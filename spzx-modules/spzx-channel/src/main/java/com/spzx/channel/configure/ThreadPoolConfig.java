package com.spzx.channel.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        int processorsCount = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                processorsCount * 2,
                processorsCount * 2,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(200),
                Executors.defaultThreadFactory(),
                //new ThreadPoolExecutor.CallerRunsPolicy()
                //自定义拒绝策略
                (runnable, executor) -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                    //再次将拒绝任务提交给线程池执行
                    executor.submit(runnable);
                }
        );
        //线程池创建,核心线程同时创建
        //threadPoolExecutor.prestartCoreThread();
        threadPoolExecutor.prestartAllCoreThreads();
        return threadPoolExecutor;
    }
}