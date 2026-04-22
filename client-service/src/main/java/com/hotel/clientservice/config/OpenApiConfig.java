package com.hotel.clientservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clientServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Client Service API")
                        .description("API de gestion des clients pour la plateforme de reservation d'hotel")
                        .version("1.0.0"));
    }
}
