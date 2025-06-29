package com.example.lambda;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

public class DBUtil {

    public static Connection getConnection() throws Exception {
        // 시크릿 이름을 실제 사용중인 이름으로 변경하세요.
        String secretName = "my-db-secret";
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
        Map<String, String> json = mapper.readValue(secretString, Map.class);

        String host = json.get("host");
        String port = json.get("port");
        String dbname = json.get("dbname");
        String username = json.get("username");
        String password = json.get("password");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?serverTimezone=UTC";

        return DriverManager.getConnection(url, username, password);
    }
}
