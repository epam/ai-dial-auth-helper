package deltix.cortex.authproxy.services;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.http.ResponseEntity;

public interface AuthProvider {
    ResponseEntity<String> getOpenidConfiguration();

    void verifyJWTToken(DecodedJWT decodedJWT) throws JwkException;

    String exchangeToken(String keyCloakToken, String idpAlias);
}
