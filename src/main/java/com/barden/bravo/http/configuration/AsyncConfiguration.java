package com.barden.bravo.http.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration class.
 */
//TODO: will review since no usage in this project. (DUE TO BARDEN JAVA LIBRARY SCHEDULER)
@Configuration
@EnableAsync
public class AsyncConfiguration {

    private static final int POOL_SIZE = 3;
    private static final int MAX_POOL_SIZE = POOL_SIZE * 5;
    private static final int QUEUE_SIZE = MAX_POOL_SIZE * 10;

    /**
     * Creates executor.
     *
     * @return Thread pool task executor.
     */
    @Bean(name = "async-executor")
    public Executor execute() {
        //Creates thread pool task executor object.
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        //Configures created executor object.
        executor.setThreadNamePrefix("[Bravo Async Thread] (HTTP) -> ");
        executor.setCorePoolSize(POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE); // Since we are aiming to have 4 cores. -> CLOUD.
        executor.setQueueCapacity(QUEUE_SIZE);

        //Initializes executor object.
        executor.initialize();

        //Returns created executor object.
        return executor;
    }

}
