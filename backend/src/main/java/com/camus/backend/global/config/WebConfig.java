package com.camus.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
		corsRegistry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods("GET", "PUT", "POST", "DELETE", "PATCH");

		//                .allowedOrigins("https://i10a709.p.ssafy.io");

		//        corsRegistry.addMapping("/**")
		//                .allowedOrigins("http://localhost:5173")
		//                .allowedOrigins("http://nocolored.store")
		//                .allowedOrigins("http://nocolored.store:18080")
		//                .allowedOrigins("http://nocolored.store:8080");
		//                .allowedOrigins("https://i10a709.p.ssafy.io");
	}
}

