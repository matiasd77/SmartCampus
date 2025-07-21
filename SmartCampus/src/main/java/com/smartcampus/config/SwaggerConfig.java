package com.smartcampus.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        try {
            return new OpenAPI()
                    .info(new Info()
                            .title("SmartCampus API")
                            .description("SmartCampus University Management System API Documentation")
                            .version("1.0.0")
                            .contact(new Contact()
                                    .name("SmartCampus Development Team")
                                    .email("support@smartcampus.al")
                                    .url("https://smartcampus.al"))
                            .license(new License()
                                    .name("MIT License")
                                    .url("https://opensource.org/licenses/MIT")))
                    .servers(List.of(
                            new Server()
                                    .url("http://localhost:8080")
                                    .description("Local Development Server")
                    ))
                    .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                    .components(new Components()
                            .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                    .type(SecurityScheme.Type.HTTP)
                                    .scheme("bearer")
                                    .bearerFormat("JWT")
                                    .description("Enter your JWT token in the format: Bearer <token>")
                                    .name("Authorization")));
        } catch (Exception e) {
            // Fallback to basic OpenAPI configuration
            return new OpenAPI()
                    .info(new Info()
                            .title("SmartCampus API")
                            .version("1.0.0"));
        }
    }
} 