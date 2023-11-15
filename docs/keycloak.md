## KeyCloak Configuration

Auth-helper uses token-exchange API in KeyCloak for access to external IdP. You need to grant permissions for the exchange
before running auth-helper: https://www.keycloak.org/docs/latest/securing_apps/#_client_to_client_permission

### Identity Provider
Go to the target realm in KeyCloak and open "Identity Providers". Choose the tab "Mappers" and create two mappers:

- *Idp*. The attribute denotes to the identity provider is used in KeyCloak. At the moment the valid value is "microsoft".
- *IdpAlias*. The attribute denotes to the alias name of the IdP configured in KeyCloak.

See the details below how to fill required fields in the mapper form to create mappers

| Name     | Sync Mode Override  | Mapper Type         | User Attribute | User Attribute Value |
|----------|---------------------|---------------------|-------------|----------------------|
| Idp      | Force               | Hardcoded Attribute | idp         | microsoft            |
| IdpAlias | Force               | Hardcoded Attribute | idpAlias    | <your_idp_alias>     |

### Client Scope

Go to "Client Scopes" in KeyCloak and choose the scope "dial". If the scope doesn't exist it should be created and be included to the client "chatbot-ui".

Go to the tab "Mappers" and create two mappers:

- *Idp*. Injects attribute value of "Idp" to the access token as a claim.
- *IdpAlias*. Injects attribute value of "IdpAlias" to the access token as a claim.

See the details below how to fill required fields in the mapper form to create mappers.

| Name       | User Attribute | Token Claim Name | Claim JSON Type | Add to access token |
|------------|----------------|------------------|-----------------|---------------------|
| Idp        | idp            | idp              | String          | On                  |
| Idp Alias  | idpAlias       | idpAlias         | String          | On                  |

Note. The rest of flags should be turned off in the mapper form.

