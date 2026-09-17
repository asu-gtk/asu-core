package kg.asugtk.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "kg.asugtk")
@EntityScan(basePackages = {"kg.asugtk.domain.entity", "kg.asugtk.telemetry.entity"})
@EnableJpaRepositories(basePackages = {"kg.asugtk.domain.repository", "kg.asugtk.telemetry.repository"})
public class AsuApplication {
    public static void main(String[] args) {
        SpringApplication.run(AsuApplication.class, args);
    }
}
