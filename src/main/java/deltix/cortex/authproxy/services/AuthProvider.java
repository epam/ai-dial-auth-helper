package deltix.cortex.authproxy.services;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.*;

public interface AuthProvider {
    ResponseEntity<String> getOpenidConfiguration();

    void verifyJWTToken(DecodedJWT decodedJWT) throws JwkException;

    String getUsername(DecodedJWT decodedJWT) throws JsonProcessingException;
}
