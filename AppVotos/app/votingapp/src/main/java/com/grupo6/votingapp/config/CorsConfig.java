package com.grupo6.votingapp.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Value("${app.allowed.origin}")
    private String AllowedOrigin;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        List<String> allowedOrigins = Arrays.asList(AllowedOrigin.split("\\s*,\\s*"));
        for (String allowedOrigin : allowedOrigins) {
            System.out.println("Allowed Origin: " + allowedOrigin);
            config.addAllowedOrigin(allowedOrigin); //! Cuidado com isto se usarmos docker
        }

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
