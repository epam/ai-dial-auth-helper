package deltix.cortex.authproxy.services;

import com.auth0.jwk.*;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import deltix.cortex.authproxy.config.AuthProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import static deltix.cortex.authproxy.utils.Utils.decodeToJson;

@Service
public class DefaultAuthProvider implements AuthProvider {
    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthProviderConfig authProviderConfig;
    private final String openidConfigurationURL;
    private volatile String openIdConfiguration = null;
    private JwkProvider jwkProvider;

    @Autowired
    public DefaultAuthProvider(AuthProviderConfig authProviderConfig) {
        this.authProviderConfig = authProviderConfig;
        openidConfigurationURL = authProviderConfig.getProviderUri() + "/.well-known/openid-configuration";
    }

    public ResponseEntity<String> getOpenidConfiguration() {
        String local = openIdConfiguration;
        if (local == null) {
            synchronized (openidConfigurationURL) {
                local = openIdConfiguration;
                if (local == null) {
                    final ResponseEntity<String> response = this.tryExchange(openidConfigurationURL, HttpMethod.GET, "", String.class, null);

                    if (response.getStatusCode() != HttpStatus.OK)
                        return response;
                    local = openIdConfiguration = response.getBody();
                }
            }
        }

        return new ResponseEntity<>(local, HttpStatus.OK);
    }

    public void verifyJWTToken(DecodedJWT decodedJWT) throws JwkException {
        Jwk jwk = this.getJwkProvider().get(decodedJWT.getKeyId());

        Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(),null);

        algorithm.verify(decodedJWT);

        if (decodedJWT.getExpiresAt().before(new Date())) {
            throw new BadRequestException("Token is expired");
        }
    }

    public String getUsername(DecodedJWT decodedJWT) throws JsonProcessingException {
        final String payloadJson = decodeToJson(decodedJWT.getPayload());

        JsonNode root = mapper.readTree(payloadJson);
        JsonNode usernameNode = root.get(authProviderConfig.getUsernamePath());

        if (usernameNode.isMissingNode()) {
            throw new NotFoundException("Configured usernamePath does not exist in token payload");
        }

        return usernameNode.asText();
    }

    private JwkProvider getJwkProvider() {
        if (this.jwkProvider == null) {
            this.jwkProvider = new JwkProviderBuilder(this.buildJwkUrl()).build();
        }

        return this.jwkProvider;
    }

    private URL buildJwkUrl() {
        String providerUri = authProviderConfig.getProviderUri();
        if (providerUri == null || providerUri.isEmpty()) {
            throw new IllegalStateException("Provider Uri is not configured");
        }

        try {
            final URI uri = new URI(providerUri).normalize();
            return uri.toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalArgumentException("Invalid jwks uri", e);
        }
    }

    private <TRequest, TResponse> ResponseEntity<TResponse> tryExchange(String uri, HttpMethod method, TRequest request, Class<TResponse> responseClass, String bearerToken, Object... uriVariables) {
        final HttpHeaders headers = new HttpHeaders();

        if (bearerToken != null) {
            headers.setBearerAuth(bearerToken);
        }

        final HttpEntity<TRequest> entity = new HttpEntity<>(request, headers);

        return rest.exchange(uri, method, entity, responseClass, uriVariables);
    }
}
