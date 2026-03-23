package com.dify.client.service;

import com.dify.client.config.DifyProperties;
import com.dify.client.dto.DifyChatRequest;
import com.dify.client.dto.DifyChatResponse;
import com.dify.client.dto.DifyWorkflowRequest;
import com.dify.client.dto.DifyWorkflowResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Service
public class DifyClientService {

    private final RestTemplate restTemplate;
    private final DifyProperties difyProperties;
    private final ObjectMapper objectMapper;

    public DifyClientService(RestTemplate restTemplate, DifyProperties difyProperties, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.difyProperties = difyProperties;
        this.objectMapper = objectMapper;
    }

    private HttpHeaders createHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey != null && !apiKey.isEmpty() ? apiKey : difyProperties.getApiKey());
        return headers;
    }

    /**
     * Chatflow - Blocking Mode
     */
    public DifyChatResponse chatBlocking(DifyChatRequest request, String apiKey) {
        request.setResponseMode("blocking");
        String url = difyProperties.getApiUrl() + "/chat-messages";
        
        HttpEntity<DifyChatRequest> entity = new HttpEntity<>(request, createHeaders(apiKey));
        
        ResponseEntity<DifyChatResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                DifyChatResponse.class
        );
        return response.getBody();
    }

    /**
     * Chatflow - Streaming Mode
     */
    public void chatStreaming(DifyChatRequest request, String apiKey, Consumer<String> onMessage) {
        request.setResponseMode("streaming");
        String url = difyProperties.getApiUrl() + "/chat-messages";

        executeStreaming(url, request, apiKey, onMessage);
    }

    /**
     * Workflow - Blocking Mode
     */
    public DifyWorkflowResponse workflowBlocking(DifyWorkflowRequest request, String apiKey) {
        request.setResponseMode("blocking");
        String url = difyProperties.getApiUrl() + "/workflows/run";
        
        HttpEntity<DifyWorkflowRequest> entity = new HttpEntity<>(request, createHeaders(apiKey));
        
        ResponseEntity<DifyWorkflowResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                DifyWorkflowResponse.class
        );
        return response.getBody();
    }

    /**
     * Workflow - Streaming Mode
     */
    public void workflowStreaming(DifyWorkflowRequest request, String apiKey, Consumer<String> onMessage) {
        request.setResponseMode("streaming");
        String url = difyProperties.getApiUrl() + "/workflows/run";

        executeStreaming(url, request, apiKey, onMessage);
    }

    /**
     * Helper to execute streaming request
     */
    private <T> void executeStreaming(String url, T requestBody, String apiKey, Consumer<String> onMessage) {
        restTemplate.execute(
                url,
                HttpMethod.POST,
                request -> {
                    HttpHeaders headers = request.getHeaders();
                    headers.putAll(createHeaders(apiKey));
                    String body = objectMapper.writeValueAsString(requestBody);
                    request.getBody().write(body.getBytes(StandardCharsets.UTF_8));
                },
                (ResponseExtractor<Void>) response -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.trim().isEmpty()) {
                                onMessage.accept(line);
                            }
                        }
                    }
                    return null;
                }
        );
    }
}
