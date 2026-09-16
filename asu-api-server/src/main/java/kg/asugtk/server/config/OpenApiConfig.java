package kg.asugtk.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI asuOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("АСУ ГТК Core API")
                        .description("REST API платформы автоматизации горно-транспортного комплекса")
                        .version("1.0.0")
                        .contact(new Contact().name("ASU GTK Team").url("https://github.com/asu-gtk")));
    }
}
