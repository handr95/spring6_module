package com.study.httpclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class RestTemplateService implements HttpClientService {

    private final RestTemplate restTemplate;

    private final AsyncRestTemplate asyncRestTemplate;
    private ObjectMapper mapper = new ObjectMapper();

    public <T> CompletableFuture<T> toCFuture(ListenableFuture<T> lf) {
        CompletableFuture<T> cf = new CompletableFuture<>();
        lf.addCallback(cf::complete, cf::completeExceptionally);
        return cf;
    }


    @Override
    public ResponseEntity<String> sendSyncGET(String baseUrl, String uri, Map<String, Object> uriVariables, Map<String, String> header){
        MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.setAll(header);
        HttpEntity<String> entity = new HttpEntity<>(headerMap);
        return restTemplate.exchange(baseUrl + uri, HttpMethod.POST, entity, String.class, uriVariables);
    }

    @Override
    public ResponseEntity<String> sendSyncPOST(String baseUrl, String uri, Map<String, Object> param) throws JsonProcessingException {
        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(param));
        return restTemplate.exchange(baseUrl + uri, HttpMethod.POST, entity, String.class);
    }

    @Override
    public CompletableFuture<ResponseEntity<String>> sendAsyncPOST(String baseUrl, String uri, Map<String, Object> param) throws JsonProcessingException {
        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(param));
        ListenableFuture<ResponseEntity<String>> future = asyncRestTemplate.exchange(baseUrl + uri, HttpMethod.POST, entity, String.class);
        CompletableFuture<ResponseEntity<String>> cf = toCFuture(future);
        return cf;
    }
}