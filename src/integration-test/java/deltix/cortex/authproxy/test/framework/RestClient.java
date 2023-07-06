package deltix.cortex.authproxy.test.framework;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestClient {
    static final RestTemplate rest = new RestTemplate();

    private RestClient() {
        throw new IllegalStateException("Utility class");
    }

    public static <TRequest, TResponse> ResponseEntity<TResponse> tryExchange(String uri, HttpMethod method, TRequest request, Class<TResponse> responseClass, String bearerToken, Object... uriVariables) {
        final HttpHeaders headers = new HttpHeaders();

        if (bearerToken != null) {
            headers.setBearerAuth(bearerToken);
        }

        final HttpEntity<TRequest> entity = new HttpEntity<>(request, headers);
        return rest.exchange(uri, method, entity, responseClass, uriVariables);
    }
}
