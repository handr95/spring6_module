package com.study.httpclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface HttpClientService {
    /**
     * GET 동기-블로킹 방식으로 요청
     *
     * @param baseUrl
     * @param uri
     * @param uriVariables url 쿼리 스트링
     * @param header       header 정보
     * @return response 정보 (body 정보가 String으로 오기 때문에 원하는 객체로 parsing 처리 필요)
     */
    ResponseEntity<String> sendSyncGET(String baseUrl, String uri, Map<String, Object> uriVariables, Map<String, String> header);

    /**
     * POST 동기-블로킹 방식으로 요청
     *
     * @param baseUrl
     * @param uri
     * @param param   request body에 넘겨줄 정보
     * @return response 정보 (body 정보가 String으로 오기 때문에 원하는 객체로 parsing 처리 필요)
     * @throws JsonProcessingException
     */
    ResponseEntity<String> sendSyncPOST(String baseUrl, String uri, Map<String, Object> param) throws JsonProcessingException;

    /**
     * POST 비동기-논블로킹(future.get() 호출 시 처리가 완료되지 전까지 블로킹됨.) 방식으로 요청
     *
     * @param baseUrl
     * @param uri
     * @param param   request body에 넘겨줄 정보
     * @throws JsonProcessingException
     * @returnr response 정보. (body 정보가 String으로 오기 때문에 원하는 객체로 parsing 처리 필요)
     */
    CompletableFuture<ResponseEntity<String>> sendAsyncPOST(String baseUrl, String uri, Map<String, Object> param) throws JsonProcessingException;
}