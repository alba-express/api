package com.albaExpress.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    private String[] urls = {
            "http://localhost:3000",
            "http://localhost:3001",
            "http://localhost:3002",
            "http://localhost:3003",
            "http://albunny.store",
            "http://43.202.122.135",
    };

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {


                registry.addMapping("/**")
                        .allowedOrigins(urls)

//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                   
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }

        };
    }
}
