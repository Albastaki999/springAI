package com.example.springAI;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration 
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow all origins for all endpoints
        registry.addMapping("/**") // Allow all endpoints
                .allowedOrigins("http://localhost:3000", "http://localhost:3002", "http://localhost:4200") // Your front-end URL (can be "*" to allow all origins)
                .allowedMethods("GET", "POST") // Methods you want to allow
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true) // Allow credentials (cookies, authorization headers)
                .maxAge(3600); // Cache the pre-flight response for 1 hour
    }
}
