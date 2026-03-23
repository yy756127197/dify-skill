package com.dify.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class DifyChatResponse {
    
    private String event;
    
    @JsonProperty("message_id")
    private String messageId;
    
    @JsonProperty("conversation_id")
    private String conversationId;
    
    private String mode;
    
    private String answer;
    
    private Map<String, Object> metadata;
    
    @JsonProperty("created_at")
    private Long createdAt;
}
