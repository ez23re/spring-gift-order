package gift.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KakaoAuthService {

    private static final Logger logger = LoggerFactory.getLogger(KakaoAuthService.class);

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    private final RestTemplate kakaoRestTemplate;

    public KakaoAuthService(RestTemplate kakaoRestTemplate) {
        this.kakaoRestTemplate = kakaoRestTemplate;
    }

    public String getAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        body.add("scope", "talk_message");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response;
        try {
            logger.info("Requesting access token with code: {}", code);
            response = kakaoRestTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            logger.info("Access token response: {}", response);
        } catch (Exception e) {
            logger.error("Failed to get access token", e);
            throw new RuntimeException("엑세스 토큰을 받을 수 없습니다.", e);
        }

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String accessToken = (String) response.getBody().get("access_token");
            logger.info("Received access token: {}", accessToken);
            return accessToken;
        } else {
            throw new RuntimeException("엑세스 토큰을 받을 수 없습니다.");
        }
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}