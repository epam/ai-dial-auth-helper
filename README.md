# AuthProxy

### Version 1.0.0
AuthProxy is a proxy service that implements OpenID-compatible Web API endpoints to avoid direct interaction with the AuthProviders' APIs, such as the Auth0 API.

One of the primary benefits of using AuthProxy is the reduction in calls to the OpenID **user_info** route. AuthProxy implements its own **user_info** route that statically configures the username extraction from the AccessToken with the **usernamePath**.

Currently, AuthProxy provides two public routes:

**GET: http://hostname/.well-known/openid-configuration**
- Returns the original response from AuthProvider, but replaces **userinfo_endpoint** url with its own **user_info** route.
- The response is cached after the first request for the AuthProvider's **openid-configuration** and remains cached since the startup of the AuthProxy application.

**GET: http://hostname/api/v1/user/user-info**
- Resolves access_token to DTS username.
- If the access token does not contain a **usernamePath** node, the endpoint returns a 404 error.
- If the token has expired or failed verification, a 400 error is returned.

**Please note that AuthProxy has been tested with the Auth0 auth provider only.**

### App Configuration
##### application.yaml


```yaml
server:
  hostUrl: "http://127.0.0.1" // Used for WellKnown userinfo_endpoint substitution, it must be complete domain url with port. 
  port: 4088 // Spring port used to start up the application

oauth2:
  providerName: "auth0" // The provider that is used
  providerUri: "https://cryptocortex-dev.us.auth0.com/" // Used for getting well-known and token verification URLs
  jwksUriPath: "jwks_uri" // Path of custom jwks_uri node in openid-configuration, Optional.
  usernamePath: "name" // Path of the custom claim that contains username, the path is to be searched in AccessToken

scheduled:
  poolSize: 2
```

For example:

AuthProvider custom claim set function:
```js
exports.onExecutePostLogin = async (event, api) => {

  api.accessToken.setCustomClaim('name', event.user.name);

  api.idToken.setCustomClaim('name', event.user.name);

};
```
application.yaml:

```yaml
...
userNamePath: "name"
...
```