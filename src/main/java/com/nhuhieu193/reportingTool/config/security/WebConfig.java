package com.nhuhieu193.reportingTool.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Áp dụng cho tất cả endpoint bắt đầu /api/
                .allowedOrigins("http://localhost:4200")  // Cho phép từ frontend Angular chạy ở cổng 4200
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Các method HTTP được cho phép
                .allowCredentials(true);
    }
}
