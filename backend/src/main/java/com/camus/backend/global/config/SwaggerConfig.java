package com.camus.backend.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;

@OpenAPIDefinition(
	info = @Info(title = "CAMUS",
		description = "CAMUS api document",
		version = "v1"))
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi getB2CMEMBERApi() {
		return GroupedOpenApi
			.builder()
			.group("B2CMEMBER")
			.pathsToMatch("/member/b2c/**")
			.build();
	}

	@Bean
	public GroupedOpenApi getB2BMEMBERApi() {
		return GroupedOpenApi
			.builder()
			.group("B2BMEMBER")
			.pathsToMatch("/member/b2b/**")
			.build();
	}

	@Bean
	public GroupedOpenApi getStatisticApi() {
		return GroupedOpenApi
			.builder()
			.group("STATISTICS")
			.pathsToMatch("/statistic/**")
			.build();
	}

	@Bean
	public GroupedOpenApi getUngroupedApis() {
		return GroupedOpenApi.builder()
			.group("UNGROUPED")
			.packagesToScan("com.camus.backend")
			.pathsToExclude("/member/b2c/**", "/member/b2b/**", "/statistic/**")
			.build();
	}
}
