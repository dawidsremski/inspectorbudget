package pl.codeve.inspectorbudget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import pl.codeve.inspectorbudget.security.recaptcha.ReCAPTCHAProperties;
import pl.codeve.inspectorbudget.storage.StorageProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({StorageProperties.class, ReCAPTCHAProperties.class})
public class InspectorbudgetApplication {

	public static void main(String[] args) {
		SpringApplication.run(InspectorbudgetApplication.class, args);
	}
}
