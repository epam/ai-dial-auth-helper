package com.epam.aidial.auth.helper.controllers;

import com.epam.aidial.auth.helper.dto.UserInfoDto;
import com.epam.aidial.auth.helper.services.AuthProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock
    private AuthProvider authProvider;

    @InjectMocks
    private UserController controller;

    @Test
    public void testGetUserInfo() throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto();
        when(authProvider.getUserInfo(eq("token"))).thenReturn(userInfoDto);
        ResponseEntity<Object> res = controller.getUserInfo("Bearer token");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(userInfoDto, res.getBody());
    }
}
