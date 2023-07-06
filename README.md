# AuthProxy

#### Version 1.0.0
##### application.yaml


```yaml
server:
  hostUrl: "http://127.0.0.1" // used for WellKnown userinfo_endpoint substitution
  port: 4088 // Spring port to startup application

oauth2:
  providerName: "auth0" // provider that is used
  providerUri: "https://cryptocortex-dev.us.auth0.com/" // used for getting wellknown and token verification urls
  usernamePath: "/name" // path of username that should be looked for in AccessToken

scheduled:
  poolSize: 2
```