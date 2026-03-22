package lk.ijse.eca.customerservice;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CustomerServiceApplication.class);
        app.setDefaultProperties(Map.of(
            "spring.application.name", "customer-service",
                "spring.config.import", "configserver:",
                "spring.cloud.config.uri", "http://localhost:9000"
        ));
        app.run(args);
    }

}
