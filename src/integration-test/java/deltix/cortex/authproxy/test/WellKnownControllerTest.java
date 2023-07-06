package deltix.cortex.authproxy.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import deltix.cortex.authproxy.test.execution.TestBase;
import deltix.cortex.authproxy.test.framework.RestClient;
import deltix.cortex.authproxy.test.helpers.TestHelper;
import deltix.cortex.authproxy.test.utils.Utils;
import org.junit.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public class WellKnownControllerTest extends TestBase {
    private String wellKnownUrl;
    private final ObjectMapper mapper = new ObjectMapper();

    public WellKnownControllerTest() {
        output("\t\tWellKnownController test.");
    }

    @Before
    public void setUp() {
        output("Preparing.");
        wellKnownUrl = TestHelper.buildAddress(netProtocol, host, serverPort, ".well-known/");
    }

    @After
    public void cleanUp() {
        try {
            output("Cleaning.");
        } catch (Exception t) {
            Utils.logExceptionAndFail(log, t);
        }
    }

    @Test(timeout = 5000)
    public void getOpenIdConfigurationTest() throws JsonProcessingException {
        final ResponseEntity<String> response = RestClient.tryExchange(wellKnownUrl + "openid-configuration", HttpMethod.GET, "", String.class, null);

        JsonNode root = mapper.readTree(response.getBody());
        JsonNode userInfoEndpointNode = root.at("/userinfo_endpoint");

        Assert.assertFalse(userInfoEndpointNode.isMissingNode());
    }
}
