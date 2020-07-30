package test.stone.communication.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池对象.
 */
@Configuration
@Slf4j
@EnableAsync
@ComponentScan("test.stone.communication.service.impl")
public class ExecutorConfig {
    @Bean("async_pool")
    public Executor asyncPromiseExecutor() {
        //机器内核数
        //int coreNumber = Runtime.getRuntime().availableProcessors();
      //  System.out.println("机器核心数:" + coreNumber);
        log.info("--------------------start thread pool.-------------------");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setKeepAliveSeconds(60);
        executor.setQueueCapacity(99999);
        executor.setThreadNamePrefix("dssp-async-");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }
}
