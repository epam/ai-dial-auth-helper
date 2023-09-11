package com.epam.aidial.auth.helper.services.keycloak;

import com.epam.aidial.auth.helper.dto.UserInfoDto;

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
