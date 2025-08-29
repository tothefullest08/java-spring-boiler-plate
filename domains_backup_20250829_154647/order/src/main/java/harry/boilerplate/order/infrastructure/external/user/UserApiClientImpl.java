package harry.boilerplate.order.infrastructure.external.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Objects;

/**
 * User Context API Client 구현체 (RestTemplate 기반)
 */
@Component
public class UserApiClientImpl implements UserApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserApiClientImpl(RestTemplate restTemplate,
                             @Value("${user.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean isValidUser(String userId) {
        String url = String.format("%s/api/users/%s", baseUrl, userId);

        ResponseEntity<UserEnvelope> response;
        int attempts = 0;
        while (true) {
            try {
                response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<UserEnvelope>() {}
                );
                break;
            } catch (RestClientException e) {
                if (++attempts >= 2) {
                    throw e;
                }
                try { Thread.sleep(200); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
            }
        }
        UserEnvelope body = response.getBody();
        if (body == null || body.getId() == null) return false;
        return Objects.nonNull(body.getId());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("unused")
    private static class UserEnvelope {
        private String id;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }
}


