package com.dify.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "dify")
public class DifyProperties {
    
    /**
     * Dify API base URL, default is http://localhost/v1
     */
    private String apiUrl = "http://localhost/v1";
    
    /**
     * API Key for the specific Dify App
     */
    private String apiKey;
}
