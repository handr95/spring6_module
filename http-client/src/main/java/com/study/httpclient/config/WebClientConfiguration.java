package com.study.httpclient.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
public class WebClientConfiguration {

    private static final int connectionTimeout = 60 * 1000; // 60 sec

    private static final Integer readTimeOut = 60;
    private static final Integer writeTimeOut = 60;

    @Bean
    public WebClient webClient() {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> {
                    configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 2); //default (256K -> 2M)
                    //configurer.defaultCodecs().enableLoggingRequestDetails(true);
                })
                .build();

        // 타임아웃 설정
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .doOnConnected(connection ->
                        connection.addHandlerLast(new ReadTimeoutHandler(readTimeOut))
                                .addHandlerLast(new WriteTimeoutHandler(writeTimeOut))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                // request Header 정보 로깅
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        clientRequest -> {
                            log.info("Request Header: {} {}", clientRequest.method(), clientRequest.url());
                            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{} : {}", name, value)));
                            return Mono.just(clientRequest);
                        }
                ))
                // response Header 정보 로깅
                .filter(ExchangeFilterFunction.ofResponseProcessor(
                        clientResponse -> {
                            log.info("Response Header: ");
                            clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{} : {}", name, value)));
                            return Mono.just(clientResponse);
                        }
                ))
                .build();
    }
}