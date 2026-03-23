package com.dify.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DifyChatRequest {
    
    /**
     * Optional input variables for the prompt
     */
    private Map<String, Object> inputs = new HashMap<>();
    
    /**
     * User's query/message
     */
    private String query;
    
    /**
     * blocking or streaming
     */
    @JsonProperty("response_mode")
    private String responseMode;
    
    /**
     * Optional conversation ID for multi-turn chats
     */
    @JsonProperty("conversation_id")
    private String conversationId;
    
    /**
     * Required user identifier
     */
    private String user;
    
    /**
     * Optional files list
     */
    private List<DifyFile> files;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifyFile {
        private String type;
        @JsonProperty("transfer_method")
        private String transferMethod;
        private String url;
        @JsonProperty("upload_file_id")
        private String uploadFileId;
    }
}
