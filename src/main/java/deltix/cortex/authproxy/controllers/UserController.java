package deltix.cortex.authproxy.controllers;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import deltix.cortex.authproxy.dto.*;
import deltix.cortex.authproxy.error.ExceptionToErrorDtoConverter;
import deltix.cortex.authproxy.error.ExceptionToHttpStatusConverter;
import deltix.cortex.authproxy.services.*;
import deltix.cortex.authproxy.utils.Utils;
import deltix.gflog.Log;
import deltix.gflog.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles user entity related requests
 */
@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final AuthProvider authProvider;
    private static final Log LOG = LogFactory.getLog(UserController.class);

    @Autowired
    public UserController(final AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    /**
     * Returns User Info
     *
     * @param authorization Authorization parameters. `Bearer Token` authorization method is supported.
     * @return User Info
     */
    @GetMapping(
            value = "/user-info",
            produces = "application/json"
    )
    public ResponseEntity<Object> getUserInfo(@RequestHeader("Authorization") String authorization) {
        try {
            final DecodedJWT decodedJWT = readToken(authorization);

            String username = this.authProvider.getUsername(decodedJWT);
            UserInfoDto userInfoDto = new UserInfoDto();

            userInfoDto.setName(username);

            return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
        } catch (Throwable e) {
            LOG.warn().append(e).commit();

            final HttpStatus status = ExceptionToHttpStatusConverter.getStatus(e);
            final ErrorDto errorDto = ExceptionToErrorDtoConverter.getErrorDto(e);

            return ResponseEntity.status(status).body(errorDto);
        }
    }

    private DecodedJWT readToken(String authorization) throws JwkException {
        String token = Utils.getTokenFromAuth(authorization);
        DecodedJWT jwt = JWT.decode(token);

        this.authProvider.verifyJWTToken(jwt);

        return jwt;
    }
}
