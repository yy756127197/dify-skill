package com.dify.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class DifyWorkflowResponse {

    @JsonProperty("workflow_run_id")
    private String workflowRunId;

    @JsonProperty("task_id")
    private String taskId;

    private WorkflowData data;

    @Data
    public static class WorkflowData {
        private String id;
        
        @JsonProperty("workflow_id")
        private String workflowId;
        
        private String status;
        
        private Map<String, Object> outputs;
        
        private String error;
        
        @JsonProperty("elapsed_time")
        private Double elapsedTime;
        
        @JsonProperty("total_tokens")
        private Integer totalTokens;
        
        @JsonProperty("total_steps")
        private Integer totalSteps;
        
        @JsonProperty("created_at")
        private Long createdAt;
        
        @JsonProperty("finished_at")
        private Long finishedAt;
    }
}
