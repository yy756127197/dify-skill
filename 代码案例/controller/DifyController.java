package com.dify.client.controller;

import com.dify.client.dto.DifyChatRequest;
import com.dify.client.dto.DifyChatResponse;
import com.dify.client.dto.DifyWorkflowRequest;
import com.dify.client.dto.DifyWorkflowResponse;
import com.dify.client.service.DifyClientService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/dify")
public class DifyController {

    @Autowired
    private DifyClientService difyClientService;
    
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Chatflow - 阻塞调用
     */
    @PostMapping("/chat/blocking")
    public ResponseEntity<DifyChatResponse> chatBlocking(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody DifyChatRequest request) {
        String apiKey = extractApiKey(authorization);
        DifyChatResponse response = difyClientService.chatBlocking(request, apiKey);
        return ResponseEntity.ok(response);
    }

    /**
     * Chatflow - 流式调用
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody DifyChatRequest request) {
        String apiKey = extractApiKey(authorization);
        // 设置较长的超时时间，比如 5 分钟
        SseEmitter emitter = new SseEmitter(300000L);
        
        executorService.execute(() -> {
            try {
                difyClientService.chatStreaming(request, apiKey, message -> {
                    try {
                        emitter.send(message);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                });
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    /**
     * Workflow - 阻塞调用
     */
    @PostMapping("/workflow/blocking")
    public ResponseEntity<DifyWorkflowResponse> workflowBlocking(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody DifyWorkflowRequest request) {
        String apiKey = extractApiKey(authorization);
        DifyWorkflowResponse response = difyClientService.workflowBlocking(request, apiKey);
        return ResponseEntity.ok(response);
    }

    /**
     * Workflow - 流式调用
     */
    @PostMapping(value = "/workflow/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter workflowStream(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody DifyWorkflowRequest request) {
        String apiKey = extractApiKey(authorization);
        SseEmitter emitter = new SseEmitter(300000L);
        
        executorService.execute(() -> {
            try {
                difyClientService.workflowStreaming(request, apiKey, message -> {
                    try {
                        emitter.send(message);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                });
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    private String extractApiKey(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return null;
    }
}
