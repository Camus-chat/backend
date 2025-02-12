package com.camus.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
		corsRegistry.addMapping("/**")
			.allowedOrigins("http://localhost:3100", "https://camus.life", "https://www.camus.life")
			.allowedMethods("GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*") // 모든 요청 헤더 허용
			.allowCredentials(true) // 인증정보 포함 (쿠키, Authorization 헤더 등)
			.maxAge(3600); // CORS 응답을 캐싱하는 시간 (초 단위)
		//                .allowedOrigins("https://i10a709.p.ssafy.io");

		//        corsRegistry.addMapping("/**")
		//                .allowedOrigins("http://localhost:5173")
		//                .allowedOrigins("http://nocolored.store")
		//                .allowedOrigins("http://nocolored.store:18080")
		//                .allowedOrigins("http://nocolored.store:8080");
		//                .allowedOrigins("https://i10a709.p.ssafy.io");
	}
}

