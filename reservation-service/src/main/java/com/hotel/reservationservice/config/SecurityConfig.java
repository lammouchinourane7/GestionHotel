package com.hotel.reservationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/*/confirm").hasRole("HOTEL_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/*/cancel").hasRole("HOTEL_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/*").hasRole("HOTEL_ADMIN")
                        .requestMatchers("/api/reservations/client/**").hasRole("HOTEL_ADMIN")
                        .requestMatchers("/api/reservations/room/**").hasRole("HOTEL_ADMIN")
                        .requestMatchers("/api/reservations/options/clients").hasRole("HOTEL_ADMIN")
                        .requestMatchers("/api/reservations/**").authenticated()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<org.springframework.security.core.GrantedAuthority> authorities = new ArrayList<>();

            Object realmAccess = jwt.getClaim("realm_access");
            if (realmAccess instanceof Map<?, ?> realmAccessMap) {
                Object rolesObject = realmAccessMap.get("roles");
                if (rolesObject instanceof Collection<?> roles) {
                    for (Object role : roles) {
                        if (role instanceof String roleName && !roleName.isBlank()) {
                            String normalized = roleName.trim().replace('-', '_').toUpperCase();
                            authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + normalized));
                        }
                    }
                }
            }

            return authorities;
        });
        return converter;
    }
}
