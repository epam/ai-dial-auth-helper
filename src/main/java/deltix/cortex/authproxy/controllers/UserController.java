package deltix.cortex.authproxy.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import deltix.cortex.authproxy.dto.ErrorDto;
import deltix.cortex.authproxy.dto.UserInfoDto;
import deltix.cortex.authproxy.error.ExceptionToErrorDtoConverter;
import deltix.cortex.authproxy.error.ExceptionToHttpStatusConverter;
import deltix.cortex.authproxy.services.AuthProvider;
import deltix.cortex.authproxy.services.IdentityProvider;
import deltix.cortex.authproxy.services.IdentityProviderFactory;
import deltix.cortex.authproxy.utils.Utils;
import deltix.gflog.Log;
import deltix.gflog.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles user entity related requests
 */
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final AuthProvider authProvider;

    private static final Log LOG = LogFactory.getLog(UserController.class);

    @Autowired
    public UserController(AuthProvider authProvider) {
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
            String token = Utils.getTokenFromAuth(authorization);
            final DecodedJWT decodedJWT = JWT.decode(token);
            authProvider.verifyJWTToken(decodedJWT);
            Claim idpClaim = decodedJWT.getClaim("idp");
            Claim idpAliasClaim = decodedJWT.getClaim("idpAlias");
            if (isClaimMissing(idpClaim) || isClaimMissing(idpAliasClaim)) {
                UserInfoDto userInfoDto = fromKeyCloakToken(decodedJWT);
                return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
            }
            IdentityProvider identityProvider = IdentityProviderFactory.createIdentityProvider(idpClaim.asString());
            String idpAccessToken = authProvider.exchangeToken(token, idpAliasClaim.asString());
            UserInfoDto userInfo = identityProvider.getUserInfo(idpAccessToken);
            userInfo.setSub(decodedJWT.getSubject());

            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } catch (Throwable e) {
            LOG.warn().append(e).commit();

            final HttpStatus status = ExceptionToHttpStatusConverter.getStatus(e);
            final ErrorDto errorDto = ExceptionToErrorDtoConverter.getErrorDto(e);

            return ResponseEntity.status(status).body(errorDto);
        }
    }

    private UserInfoDto fromKeyCloakToken(DecodedJWT jwt) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setSub(jwt.getSubject());
        userInfoDto.setName(jwt.getClaim("preferred_username").asString());
        userInfoDto.setEmail(jwt.getClaim("email").asString());
        return userInfoDto;
    }

    private static boolean isClaimMissing(Claim claim) {
        return claim == null || claim.isMissing() || claim.isNull();
    }
}
