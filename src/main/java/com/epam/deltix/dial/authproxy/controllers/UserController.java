package com.epam.deltix.dial.authproxy.controllers;

import com.epam.deltix.dial.authproxy.dto.ErrorDto;
import com.epam.deltix.dial.authproxy.dto.UserInfoDto;
import com.epam.deltix.dial.authproxy.error.ExceptionToErrorDtoConverter;
import com.epam.deltix.dial.authproxy.error.ExceptionToHttpStatusConverter;
import com.epam.deltix.dial.authproxy.services.AuthProvider;
import com.epam.deltix.dial.authproxy.utils.Utils;
import com.epam.deltix.gflog.api.Log;
import com.epam.deltix.gflog.api.LogFactory;
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
            UserInfoDto userInfo = authProvider.getUserInfo(token);
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } catch (Throwable e) {
            LOG.warn().append(e).commit();

            final HttpStatus status = ExceptionToHttpStatusConverter.getStatus(e);
            final ErrorDto errorDto = ExceptionToErrorDtoConverter.getErrorDto(e);

            return ResponseEntity.status(status).body(errorDto);
        }
    }
}
