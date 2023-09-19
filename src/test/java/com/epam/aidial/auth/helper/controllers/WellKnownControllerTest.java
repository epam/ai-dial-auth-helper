package com.epam.aidial.auth.helper.controllers;

import com.epam.aidial.auth.helper.config.ServerConfig;
import com.epam.aidial.auth.helper.services.AuthProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WellKnownControllerTest {
    @Mock
    private ServerConfig serverConfig;
    @Mock
    private AuthProvider authProvider;

    @InjectMocks
    private WellKnownController controller;

    @Test
    public void testGetOpenIdConfiguration_Success() {
        ResponseEntity<String> openIdConfigRes = new ResponseEntity<>("{\"userinfo_endpoint\": \"http://host1/user-info\"}", HttpStatus.OK);
        when(authProvider.getOpenidConfiguration()).thenReturn(openIdConfigRes);
        when(serverConfig.getHostUrl()).thenReturn("http://localhost");
        ResponseEntity<Object> res = controller.getOpenIdConfiguration();
        assertNotNull(res);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("{\"userinfo_endpoint\":\"http://localhost/api/v1/user/user-info\"}", res.getBody());
    }

    @Test
    public void testGetOpenIdConfiguration_Fail() {
        ResponseEntity<String> openIdConfigRes = new ResponseEntity<>("resource not found", HttpStatus.NOT_FOUND);
        when(authProvider.getOpenidConfiguration()).thenReturn(openIdConfigRes);
        ResponseEntity<Object> res = controller.getOpenIdConfiguration();
        assertNotNull(res);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertEquals("resource not found", res.getBody());
    }

}
