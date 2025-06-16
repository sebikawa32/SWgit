package com.jose.ticket.global.security;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AwsSecretsManager {

    private final SecretsManagerClient client;
    private final ObjectMapper objectMapper;

    public AwsSecretsManager() {
        this.client = SecretsManagerClient.builder()
                .region(Region.AP_NORTHEAST_2) // 서울 리전
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, String> getSecretMap(String secretName) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse response = client.getSecretValue(request);
        try {
            return objectMapper.readValue(response.secretString(), Map.class);
        } catch (Exception e) {
            throw new RuntimeException("❌ 시크릿 불러오기 실패: " + e.getMessage());
        }
    }
}
