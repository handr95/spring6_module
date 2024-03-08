package com.study.httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.httpclient.service.HttpClientService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class WebclientServiceTest {

    public static ObjectMapper objectMapper;

    public static MockWebServer mockBackEnd;

    @Autowired
    @Qualifier("webClientService")
    private HttpClientService httpClientService;

    @BeforeAll
    static void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        final Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) {
                switch (request.getPath()) {
                    case "/api/webclient/1": {
                        log.info("call /api/webclient/1");
                        return new MockResponse()
                                .setBodyDelay(30, TimeUnit.SECONDS)
                                .setResponseCode(200)
                                .setBody("/api/webclient/1");
                    }
                    case "/api/webclient/2": {
                        log.info("call /api/webclient/2");
                        return new MockResponse()
                                .setBodyDelay(30, TimeUnit.SECONDS)
                                .setResponseCode(200)
                                .setBody("/api/webclient/2");
                    }
                    case "/api/webclient/timeout": {
                        log.info("call /api/webclient/timeout");
                        return new MockResponse()
                                .setBodyDelay(2, TimeUnit.MINUTES)
                                .setResponseCode(200)
                                .setBody("/api/webclient/timeout");
                    }
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        mockBackEnd.setDispatcher(dispatcher);

    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("webClient GET 통신 동작 확인")
    void webClientGETTest() throws Exception {
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("param", "param");

        Map<String, String> header = new HashMap<>();
        header.put("headerTest", "headerTest");

        ResponseEntity<String> response = httpClientService.sendSyncGET("http://localhost:" + mockBackEnd.getPort(), "/api/webclient/1", uriVariables, header);
        log.info("something else");

        log.info(response.getBody());

        System.out.println(Runtime.getRuntime().availableProcessors());
        Thread.getAllStackTraces()
                .keySet()
                .stream()
                .collect(Collectors.toList()).forEach(d -> System.out.println(d.toString()));
    }

    @Test
    @DisplayName("webClient POST 통신 동작 확인")
    void webClientPOSTTest() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("param", "param");

        CompletableFuture<ResponseEntity<String>> response = httpClientService.sendAsyncPOST("http://localhost:" + mockBackEnd.getPort(), "/api/webclient/1", requestBody);
        log.info("something else");

        ResponseEntity<String> response2 = httpClientService.sendSyncPOST("http://localhost:" + mockBackEnd.getPort(), "/api/webclient/2", requestBody);
        log.info("something else");

        log.info(response2.getBody());
        log.info(response.get().getBody());

        Thread.sleep(60000L);
    }

    @Test
    @DisplayName("webClient readTimeout 확인")
    void webClientTimeOutTest() throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("param", "param");
        Assertions.assertThrows(WebClientResponseException.class, () -> {
            CompletableFuture<ResponseEntity<String>> response = httpClientService.sendAsyncPOST("http://localhost:" + mockBackEnd.getPort(), "/api/webclient/timeout", requestBody);
            log.info("something else");

            ResponseEntity<String> response2 = httpClientService.sendSyncPOST("http://localhost:" + mockBackEnd.getPort(), "/api/webclient/timeout", requestBody);
            log.info("something else");

            Thread.sleep(60000L);
        });
    }
}