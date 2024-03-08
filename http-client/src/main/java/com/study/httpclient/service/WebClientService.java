package com.study.httpclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebClientService implements HttpClientService {
    private final WebClient webClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseEntity<String> sendSyncGET(String baseUrl, String uri, Map<String, Object> uriVariables, Map<String, String> header) {
        return webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri, uriVariables)
                .headers(headersConsumer -> headersConsumer.setAll(header))
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    @Override
    public ResponseEntity<String> sendSyncPOST(String baseUrl, String uri, Map<String, Object> param) throws JsonProcessingException {
        return webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(objectMapper.writeValueAsString(param)))
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    @Override
    public CompletableFuture<ResponseEntity<String>> sendAsyncPOST(String baseUrl, String uri, Map<String, Object> param) throws JsonProcessingException {
        return webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(objectMapper.writeValueAsString(param)))
                .retrieve()
                .toEntity(String.class)
                .toFuture();
    }
}