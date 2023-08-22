package deltix.cortex.authproxy.services;

public class IdentityProviderFactory {

    public static IdentityProvider createIdentityProvider(String idp) {
        switch (idp) {
            case "microsoft":
                return new MicrosoftIdentityProvider();
            default:
                throw new IllegalArgumentException("Unsupported identity provider: " + idp);
        }
    }
}
