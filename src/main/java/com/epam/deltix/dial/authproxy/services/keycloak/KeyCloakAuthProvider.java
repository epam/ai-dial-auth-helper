package com.epam.deltix.dial.authproxy.services.keycloak;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.epam.deltix.dial.authproxy.config.AuthProviderConfig;
import com.epam.deltix.dial.authproxy.dto.UserInfoDto;
import com.epam.deltix.dial.authproxy.services.BaseAuthProvider;
import com.epam.deltix.dial.authproxy.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.epam.deltix.dial.authproxy.utils.Utils.isClaimMissing;

@Service
public class KeyCloakAuthProvider extends BaseAuthProvider {

    public KeyCloakAuthProvider(AuthProviderConfig authProviderConfig) {
        super(authProviderConfig);
    }

    private String exchangeToken(String keyCloakToken, String idpAlias) {
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

    @Override
    public UserInfoDto getUserInfo(String accessToken) throws Exception {
        String token = Utils.getTokenFromAuth(accessToken);
        final DecodedJWT decodedJWT = JWT.decode(token);
        verifyJWTToken(decodedJWT);
        Claim idpClaim = decodedJWT.getClaim("idp");
        Claim idpAliasClaim = decodedJWT.getClaim("idpAlias");
        if (isClaimMissing(idpClaim) || isClaimMissing(idpAliasClaim)) {
            fromKeyCloakToken(decodedJWT);
        }
        IdentityProvider identityProvider = IdentityProviderFactory.createIdentityProvider(idpClaim.asString());
        String idpAccessToken = exchangeToken(token, idpAliasClaim.asString());
        UserInfoDto userInfo = identityProvider.getUserInfo(idpAccessToken);
        userInfo.setSub(decodedJWT.getSubject());
        return userInfo;
    }

    private UserInfoDto fromKeyCloakToken(DecodedJWT jwt) {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setSub(jwt.getSubject());
        userInfoDto.setName(jwt.getClaim("preferred_username").asString());
        userInfoDto.setEmail(jwt.getClaim("email").asString());
        return userInfoDto;
    }
}
