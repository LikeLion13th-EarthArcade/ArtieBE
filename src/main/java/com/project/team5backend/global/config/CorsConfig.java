package com.project.team5backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Collections;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean("customCorsConfigurationSource")
    @Primary
    public CorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 Origin List
        ArrayList<String> allowedOrigins = new ArrayList<>();
        allowedOrigins.add("http://localhost:8080");
        allowedOrigins.add("http://localhost:5173");
        allowedOrigins.add("http://localhost:5174");
        allowedOrigins.add("https://artie-blond.vercel.app");
        allowedOrigins.add("https://artiee.store");
        allowedOrigins.add("https://api.artiee.store");

        configuration.setAllowedOriginPatterns(allowedOrigins);

        // 허용할 HTTP METHOD
        ArrayList<String> allowedHttpMethods = new ArrayList<>();
        allowedHttpMethods.add("OPTIONS");
        allowedHttpMethods.add("GET");
        allowedHttpMethods.add("POST");
        allowedHttpMethods.add("PATCH");
        allowedHttpMethods.add("DELETE");

        configuration.setAllowedMethods(allowedHttpMethods);

        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
