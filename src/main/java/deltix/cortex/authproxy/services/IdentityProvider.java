package deltix.cortex.authproxy.services;

import deltix.cortex.authproxy.dto.UserInfoDto;

public interface IdentityProvider {
    UserInfoDto getUserInfo(String accessToken);
}
