# Overview

AuthProxy is a proxy service that implements OpenID-compatible Web API endpoints to avoid direct interaction with the AuthProviders' APIs, such as the KeyCloak API.

One of the primary benefits of using AuthProxy is the enrichment of user info with additional fields such as **jobTitile** and **picture**.
AuthProxy implements its own **user_info** route that makes a call to the target Idp configured in KeyCloak.

Notes. KeyCloak should be configured to inject into the access token the additional claims:
- **idp**. Identity provider code name. Supported values are **microsoft**.
- **idpAlias**. The alias to Idp configured in KeyCloak realm.

Currently, AuthProxy provides two public routes:

**GET: http://hostname/.well-known/openid-configuration**
- Returns the original response from AuthProvider, but replaces **userinfo_endpoint** url with its own **user_info** route.
- The response is cached after the first request for the AuthProvider's **openid-configuration** and remains cached since the startup of the AuthProxy application.

**GET: http://hostname/api/v1/user/user-info**
- Authorization header is required in the request
- Returns user info with additional fields like job title and picture provided by IdP in KeyCloak
- If the claims either idp or idpAlias is not provided the user info is constructed from the claims of the access token
- If the token has expired or failed verification, a 400 error is returned.

**Please note that AuthProxy has been tested with Microsoft Graph API only.**

# Developer env

- Open JDK 11+
- Gradle 7+

# Build

```
 ./gradlew clean build
```

# Test

```
 ./gradlew test
```

# Run

```
  ./gradlew run
```

# Configure
##### application.yaml


```yaml
server:
  hostUrl: "http://127.0.0.1" // Used for WellKnown userinfo_endpoint substitution, it must be complete domain url with port. 
  port: 4088 // Spring port used to start up the application

oauth2:
  providerUri: "http://localhost:8080/realms/your_realm" // Used for getting well-known and token verification URLs
  clientId: "your_client_id" // keyCloak client id
  clientSecret: "your_client_secret" // KeyCloak client secret
  jwksUriPath: "jwks_uri" // Path of custom jwks_uri node in openid-configuration, Optional.

scheduled:
  poolSize: 2
```