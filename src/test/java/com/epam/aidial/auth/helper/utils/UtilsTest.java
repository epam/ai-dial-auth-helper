package com.epam.aidial.auth.helper.utils;

import com.auth0.jwt.interfaces.Claim;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class UtilsTest {
    @Test
    public void testGetTokenFromAuth_1() {
        String token = Utils.getTokenFromAuth("Bearer token");
        assertEquals("token",  token);
    }

    @Test
    public void testGetTokenFromAuth_2() {
        String token = Utils.getTokenFromAuth("Basic token");
        assertNull(token);
    }

    @Test
    public void testIsClaimMissing_Null() {
        assertTrue(Utils.isClaimMissing(null));
    }

    @Test
    public void testIsClaimMissing_isMissing() {
        Claim claim = Mockito.mock(Claim.class);
        when(claim.isMissing()).thenReturn(true);
        assertTrue(Utils.isClaimMissing(claim));
    }

    @Test
    public void testIsClaimMissing_isNull() {
        Claim claim = Mockito.mock(Claim.class);
        when(claim.isNull()).thenReturn(true);
        assertTrue(Utils.isClaimMissing(claim));
    }

    @Test
    public void testIsClaimMissing_NotMissed() {
        Claim claim = Mockito.mock(Claim.class);
        assertFalse(Utils.isClaimMissing(claim));
    }

    @Test
    public void testIsNullOrEmpty() {
        assertTrue(Utils.isNullOrEmpty(null));
        assertTrue(Utils.isNullOrEmpty(""));
        assertFalse(Utils.isNullOrEmpty("abc"));
    }
}
