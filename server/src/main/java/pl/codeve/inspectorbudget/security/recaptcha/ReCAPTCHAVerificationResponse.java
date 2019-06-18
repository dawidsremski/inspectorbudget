package pl.codeve.inspectorbudget.security.recaptcha;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ReCAPTCHAVerificationResponse {
    @NotBlank
    private Boolean success;
    @NotBlank
    private LocalDateTime challengeTs;
    @NotBlank
    private String hostname;
    private List<String> errorCodes;
}
