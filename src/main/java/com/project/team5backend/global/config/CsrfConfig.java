package com.project.team5backend.global.config;

import com.project.team5backend.global.security.repository.CustomCookieCsrfTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsrfConfig {

    @Bean
    public CustomCookieCsrfTokenRepository customCookieCsrfTokenRepository() {
        return new CustomCookieCsrfTokenRepository();
    }
}
