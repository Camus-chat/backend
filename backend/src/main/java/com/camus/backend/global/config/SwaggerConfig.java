package com.camus.backend.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;

@OpenAPIDefinition(
	info = @Info(title = "CAMUS",
		description = "CAMUS api document",
		version = "v1"),
	servers = {
		@Server(url = "https://api.camus.life", description = "be-dev"),
		@Server(url = "http://localhost:8080", description = "로컬실행용")
	}
)

@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi getMemberApi() {
		return GroupedOpenApi
			.builder()
			.group("MEMBER")
			.pathsToMatch("/member/**")
			.build();
	}

	@Bean
	public GroupedOpenApi getGuestApi() {
		return GroupedOpenApi
				.builder()
				.group("GUEST")
				.pathsToMatch("/guest/**")
				.build();
	}
	@Bean
	public GroupedOpenApi getReissueApi() {
		return GroupedOpenApi
				.builder()
				.group("REISSUE")
				.pathsToMatch("/reissue/**")
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
	public GroupedOpenApi getChatDataApi() {
		return GroupedOpenApi
				.builder()
				.group("CHAT")
				.pathsToMatch("/chat/**")
				.build();
	}

	@Bean
	public GroupedOpenApi getRoomApi() {
		return GroupedOpenApi
				.builder()
				.group("ROOM")
				.pathsToMatch("/room/**")
				.build();
	}

	@Bean
	public GroupedOpenApi getChannelApi() {
		return GroupedOpenApi
				.builder()
				.group("CHANNEL")
				.pathsToMatch("/channel/**")
				.build();
	}


//	@Bean
//	public GroupedOpenApi getUngroupedApis() {
//		return GroupedOpenApi.builder()
//			.group("UNGROUPED")
//			.packagesToScan("com.camus.backend")
//			.pathsToExclude("/member/**", "/statistic/**", "/chat/**", "/test/**", "/room/**", "/channel/**", "/filter/**", "/guest/**", "/reissue/**")
//			.build();
//	}


}
