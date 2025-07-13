package org.galymzhan.financetrackerbackend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Finance Tracker API",
                version = "1.0",
                description = "REST API for personal finance tracking with accounts, operations, categories, and tags",
                contact = @Contact(
                        name = "Galymzhan",
                        email = "zhangazy2004@gmail.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Development server"),
                @Server(url = "https://api.finance-tracker.com", description = "Production server")
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Finance Tracker API")
                        .version("1.0")
                        .description("REST API for personal finance tracking with accounts, operations, categories, and tags")
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
    }
} 