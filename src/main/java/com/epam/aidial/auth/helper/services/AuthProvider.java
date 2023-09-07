package com.epam.aidial.auth.helper.services;

import com.epam.aidial.auth.helper.dto.UserInfoDto;
import org.springframework.http.ResponseEntity;

public interface AuthProvider {
    ResponseEntity<String> getOpenidConfiguration();

    UserInfoDto getUserInfo(String accessToken) throws Exception;
}
