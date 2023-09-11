package com.epam.aidial.auth.helper.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth2")
public class AuthProviderConfig {
    private String providerUri;
    private String jwksUriPath;
    private String clientId;

    private String clientSecret;

    public String getProviderUri() {
        return providerUri;
    }

    public void setProviderUri(String providerUri) {
        this.providerUri = providerUri;
    }

    public String getJwksUriPath() {
        return jwksUriPath;
    }

    public void setJwksUriPath(String jwksUriPath) {
        this.jwksUriPath = jwksUriPath;
    }

    public String getClientId() {
        return clientId;
    }

    public AuthProviderConfig setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public AuthProviderConfig setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }
}
