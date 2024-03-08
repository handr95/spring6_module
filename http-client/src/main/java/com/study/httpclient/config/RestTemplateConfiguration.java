package com.study.httpclient.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class RestTemplateConfiguration {

    private static final int connectionTimeout = 60 * 1000; // 60 sec
    private static final int readTimeout = 60 * 1000; // 60 sec

    private Integer asyncCorePoolSize = 4;
    private Integer asyncMaxPoolSize = 1000;
    private Integer asyncQueueCapacity = 1000;

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);

        return new RestTemplate(factory);
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(asyncCorePoolSize);
        taskExecutor.setMaxPoolSize(asyncMaxPoolSize);
        taskExecutor.setQueueCapacity(asyncQueueCapacity);
        taskExecutor.initialize();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setTaskExecutor(taskExecutor);
        requestFactory.setConnectTimeout(connectionTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return new AsyncRestTemplate(requestFactory, restTemplate());
    }
}