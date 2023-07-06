package deltix.cortex.authproxy.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import deltix.cortex.authproxy.config.ServerConfig;
import deltix.cortex.authproxy.dto.ErrorDto;
import deltix.cortex.authproxy.error.ExceptionToErrorDtoConverter;
import deltix.cortex.authproxy.error.ExceptionToHttpStatusConverter;
import deltix.cortex.authproxy.services.AuthProvider;
import deltix.gflog.Log;
import deltix.gflog.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;

/**
 * Handles '.well-known' requests.
 */
@RestController
@RequestMapping(".well-known/")
public class WellKnownController {
    private final ServerConfig serverConfig;
    private final AuthProvider authProvider;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Log LOG = LogFactory.getLog(WellKnownController.class);

    @Autowired
    public WellKnownController(ServerConfig serverConfig, AuthProvider authProvider) {
        this.serverConfig = serverConfig;
        this.authProvider = authProvider;
    }

    /**
     * The well known endpoint can be used to retrieve information for OpenID Connect clients.
     *
     * Method also substitutes "userinfo_endpoint" field with configured "userinfo_endpoint"
     */
    @CrossOrigin(origins = "*")
    @GetMapping(
            value = "/openid-configuration",
            produces = "application/json"
    )
    public ResponseEntity<Object> getOpenIdConfiguration() {
        try {
            final ResponseEntity<String> openIdConfiguration = authProvider.getOpenidConfiguration();
            if (openIdConfiguration.getStatusCode() != HttpStatus.OK)
                return new ResponseEntity<>(openIdConfiguration.getBody(), openIdConfiguration.getStatusCode());

            JsonNode root = mapper.readTree(openIdConfiguration.getBody());
            String ret = mapper.writeValueAsString(((ObjectNode) root).put("userinfo_endpoint", serverConfig.getHostUrl() + ":" + serverConfig.getPort() + "/" + "api/v1/user/user-info"));

            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch (RestClientResponseException e) {
            return new ResponseEntity<>(e.getResponseBodyAsString(), HttpStatus.valueOf(e.getRawStatusCode()));
        } catch (Throwable e) {
            LOG.warn().append(e).commit();

            final HttpStatus status = ExceptionToHttpStatusConverter.getStatus(e);
            final ErrorDto errorDto = ExceptionToErrorDtoConverter.getErrorDto(e);

            return ResponseEntity.status(status).body(errorDto);
        }
    }
}
