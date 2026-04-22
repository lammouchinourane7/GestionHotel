package com.hotel.reservationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reservationServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Reservation Service API")
                        .description("API de gestion des reservations avec OpenFeign et RabbitMQ")
                        .version("1.0.0"));
    }
}
