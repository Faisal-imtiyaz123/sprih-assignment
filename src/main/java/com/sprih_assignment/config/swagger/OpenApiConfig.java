package com.sprih_assignment.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI eventManagementAPI() {
        return new OpenAPI()
                .openapi("3.0.0")
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server")
                ))
                .info(new Info()
                        .title("Event Management API")
                        .description("REST API for managing Email, SMS, and Push events")
                        .version("1.0.0"));
    }
    
}