package com.jose.ticket.config;

import com.jose.ticket.global.security.AwsSecretsManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class OAuth2ClientConfig {

    private final AwsSecretsManager secretsManager;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        Map<String, String> secret = secretsManager.getSecretMap("ticketplanet/google-oauth");

        String clientId = secret.get("google.clientId");
        String clientSecret = secret.get("google.clientSecret");

        System.out.println("clientId = " + clientId);
        System.out.println("clientSecret = " + clientSecret);

        if (clientId == null || clientId.isEmpty()) {
            throw new IllegalStateException("Google OAuth clientId is null or empty");
        }
        if (clientSecret == null || clientSecret.isEmpty()) {
            throw new IllegalStateException("Google OAuth clientSecret is null or empty");
        }

        ClientRegistration registration = ClientRegistration.withRegistrationId("google")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri("{baseUrl}/login/oauth2/code/google")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("email", "profile")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("email")
                .clientName("Google")
                .build();

        return new InMemoryClientRegistrationRepository(registration);
    }
}
