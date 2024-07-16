package com.main.vlbi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/authenticate")
                .allowedMethods("OPTIONS", "GET", "PUT", "POST", "DELETE")
	          .allowedOrigins("https://" +  System.getenv("CLIENT_IP")  + ":" + System.getenv("CLIENT_PORT"))
                .allowedHeaders("*");
    }
    
    
    @Bean
    public RestTemplate getRestTemplate() {
       return new RestTemplate();
    }
}
