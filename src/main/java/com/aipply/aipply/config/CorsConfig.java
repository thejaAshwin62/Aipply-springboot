package com.aipply.aipply.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Add your Vercel domain
        config.addAllowedOrigin("https://aipply-silk.vercel.app");
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://localhost:3001");
        
        // Allow credentials
        config.setAllowCredentials(true);
        
        // Allow specific HTTP methods
        config.addAllowedMethod("*");
        
        // Allow specific headers
        config.addAllowedHeader("*");
        
        // Add exposed headers
        config.addExposedHeader("Authorization");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
