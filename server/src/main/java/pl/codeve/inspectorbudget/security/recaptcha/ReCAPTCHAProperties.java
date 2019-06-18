package pl.codeve.inspectorbudget.security.recaptcha;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "recaptcha")
public class ReCAPTCHAProperties {

    /** reCAPTCHA secret key */
    private String secretKey;
}
