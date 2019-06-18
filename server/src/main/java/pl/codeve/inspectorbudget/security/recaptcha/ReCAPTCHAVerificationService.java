package pl.codeve.inspectorbudget.security.recaptcha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class ReCAPTCHAVerificationService {

    @Value("${recaptcha.verification-url}")
    String reCAPTCHAVerificationURL;
    @Value("${recaptcha.secret-key}")
    String reCAPTCHASecretKey;

    public ResponseEntity<ReCAPTCHAVerificationResponse> verify(String reCAPTCHAResponse) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("secret", reCAPTCHASecretKey);
        map.add("response", reCAPTCHAResponse);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.postForEntity(
                reCAPTCHAVerificationURL,
                request,
                ReCAPTCHAVerificationResponse.class);
    }
}