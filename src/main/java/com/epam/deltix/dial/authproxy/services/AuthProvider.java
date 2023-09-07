package com.epam.deltix.dial.authproxy.services;

import com.epam.deltix.dial.authproxy.dto.UserInfoDto;
import org.springframework.http.ResponseEntity;

public interface AuthProvider {
    ResponseEntity<String> getOpenidConfiguration();

    UserInfoDto getUserInfo(String accessToken) throws Exception;
}
