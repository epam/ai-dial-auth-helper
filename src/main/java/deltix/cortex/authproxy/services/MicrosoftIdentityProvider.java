package deltix.cortex.authproxy.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import deltix.cortex.authproxy.dto.UserInfoDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

public class MicrosoftIdentityProvider implements IdentityProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public UserInfoDto getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.add("Accept", "application/json");
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("https://graph.microsoft.com/v1.0/me", HttpMethod.GET, requestEntity, String.class);
        JsonNode root;
        try {
            root = mapper.readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        UserInfoDto userInfo = new UserInfoDto();
        String photo = getUserPhoto(accessToken);
        userInfo.setPicture(photo);
        userInfo.setJobTitle(root.get("jobTitle").textValue());
        userInfo.setEmail(root.get("mail").textValue());
        userInfo.setName(root.get("displayName").textValue());
        return userInfo;
    }

    private String getUserPhoto(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<byte[]> response = restTemplate.exchange("https://graph.microsoft.com/v1.0/me/photos/48x48/$value", HttpMethod.GET, requestEntity, byte[].class);
            byte[] content = response.getBody();
            String encodedString = Base64.getEncoder().encodeToString(content);
            return "data:image/jpeg;base64, " + encodedString;
        } catch (HttpClientErrorException.NotFound e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null  && errorMsg.contains("ImageNotFound")) {
                return null;
            }
            throw e;
        }
    }
}
