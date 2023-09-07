package com.epam.deltix.dial.authproxy.services.keycloak;

class IdentityProviderFactory {

    private static final MicrosoftIdentityProvider MICROSOFT_IDENTITY_PROVIDER = new MicrosoftIdentityProvider();

    public static IdentityProvider createIdentityProvider(String idp) {
        switch (idp) {
            case "microsoft":
                return MICROSOFT_IDENTITY_PROVIDER;
            default:
                throw new IllegalArgumentException("Unsupported identity provider: " + idp);
        }
    }
}
