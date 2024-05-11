package com.codeanalyser.shop.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Adjust "/**" to be more specific if needed to restrict to specific endpoints
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // Allows all endpoints to be accessed from this origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "UPGRADE") // Allows these methods from the frontend
                .allowedHeaders("*") // Allows all headers
                .allowCredentials(true); // Allow credentials, adjust if you have specific needs regarding cookies, authorization headers etc.
    }
}
