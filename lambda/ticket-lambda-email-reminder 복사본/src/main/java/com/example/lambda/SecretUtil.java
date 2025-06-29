package com.example.lambda;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class SecretUtil {
    public static Map<String, String> getSecret() throws Exception {
        String secretName = "ticketplanet/credentials";  // 시크릿 이름 하드코딩
        Region region = Region.AP_NORTHEAST_2;

        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
        GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        String secretString = getSecretValueResponse.secretString();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(secretString, Map.class);
    }
}
