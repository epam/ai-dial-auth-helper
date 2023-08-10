package deltix.cortex.authproxy.services;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import deltix.cortex.authproxy.config.AuthProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.BadRequestException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import static deltix.cortex.authproxy.utils.Utils.isNullOrEmpty;

@Service
public class DefaultAuthProvider implements AuthProvider {
    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthProviderConfig authProviderConfig;
    private final String openidConfigurationURL;
    private final Object jwkProviderLock = new Object();
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

    public String exchangeToken(String keyCloakToken, String idpAlias) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", authProviderConfig.getClientId());
        map.add("client_secret", authProviderConfig.getClientSecret());
        map.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        map.add("subject_token", keyCloakToken);
        map.add("requested_token_type", "urn:ietf:params:oauth:token-type:access_token");
        map.add("requested_issuer", idpAlias);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = rest.postForEntity(authProviderConfig.getProviderUri() + "/protocol/openid-connect/token", request, String.class);
        JsonNode root;
        try {
            root = mapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return root.get("access_token").asText();
    }

    private JwkProvider getJwkProvider() {
        JwkProvider local = this.jwkProvider;

        if (local == null) {
            synchronized (jwkProviderLock) {
                local = this.jwkProvider;
                if (local == null) {
                    if (!isNullOrEmpty(this.authProviderConfig.getJwksUriPath())) {
                        try {
                            ResponseEntity<String> openIdConfiguration = this.getOpenidConfiguration();
                            if (openIdConfiguration.getStatusCode() == HttpStatus.OK) {
                                JsonNode root = mapper.readTree(openIdConfiguration.getBody());
                                JsonNode jwksUriNode = root.get(this.authProviderConfig.getJwksUriPath());

                                if (!jwksUriNode.isMissingNode()) {
                                    local = new JwkProviderBuilder(this.buildJwkUrl(jwksUriNode.textValue())).build();
                                }
                            }

                        } catch (JsonProcessingException ignore) {}
                    }

                    if (local == null) {
                        local = new JwkProviderBuilder(this.authProviderConfig.getProviderUri()).build();
                    }

                    this.jwkProvider = local;
                }
            }
        }

        return local;
    }

    private URL buildJwkUrl(String jwksUri) {
        if (isNullOrEmpty(jwksUri)) {
            throw new IllegalStateException("Invalid jwks uri");
        }

        try {
            final URI uri = new URI(jwksUri).normalize();
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
