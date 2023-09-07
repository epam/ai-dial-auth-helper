package com.epam.deltix.dial.authproxy.services.keycloak;

import com.epam.deltix.dial.authproxy.dto.UserInfoDto;

/**
 * Identity provider configured in KeyCloak realm.
 */
interface IdentityProvider {

    /**
     * Returns user info response.
     *
     * @param accessToken JWT token
     */
    UserInfoDto getUserInfo(String accessToken);
}
