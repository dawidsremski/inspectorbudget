package pl.codeve.inspectorbudget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import pl.codeve.inspectorbudget.storage.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class InspectorbudgetApplication {

	public static void main(String[] args) {
		SpringApplication.run(InspectorbudgetApplication.class, args);
	}
}
