package com.schedulify.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:Schedulify}")
    private String applicationName;

    @Bean
    public OpenAPI schedulifyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName)
                        .description("Schedulify Backend API Documentation")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Schedulify Team")
                                .email("contact@schedulify.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("/").description("Default Server URL")
                ));
    }
} 