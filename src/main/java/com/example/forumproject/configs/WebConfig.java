package com.example.forumproject.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry
          .addResourceHandler("/static/assets/profile/**")
                .addResourceLocations("classpath:/static/assets/profile/");
    }
}
