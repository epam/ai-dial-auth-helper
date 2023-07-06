package deltix.cortex.authproxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "oauth2")
public class AuthProviderConfig {
    private String providerUri;
    private String usernamePath;

    public String getProviderUri() {
        return providerUri;
    }

    public void setProviderUri(String providerUri) {
        this.providerUri = providerUri;
    }

    public String getUsernamePath() { return usernamePath; }

    public void setUsernamePath(String usernamePath) {
        this.usernamePath = usernamePath;
    }
}
