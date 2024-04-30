package com.camus.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.camus.backend.jwt.CustomLogoutFilter;
import com.camus.backend.jwt.JwtAuthenticationFilter;
import com.camus.backend.jwt.JwtSettings;
import com.camus.backend.jwt.JwtTokenProvider;
import com.camus.backend.jwt.LoginFilter;
import com.camus.backend.service.RedisService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	//AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
	private final AuthenticationConfiguration authenticationConfiguration;

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;
	private final JwtSettings jwtSettings;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtTokenProvider jwtTokenProvider,
		RedisService redisService, JwtSettings jwtSettings) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisService = redisService;
		this.jwtSettings = jwtSettings;
	}

	//AuthenticationManager Bean 등록
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// csrf disable
		// 클라이언트 기반 인증 방식 (토큰 등)을 사용하는 경우라서 해도 됌
		http
			.csrf((auth) -> auth.disable());

		// form 로그인 방식 disable (기본 로그인 창 제거)
		http
			.formLogin((auth) -> auth.disable());

		// http basic 인증 방식 disable (Http basic Auth 기반 로그인 창 제거)
		http
			.httpBasic((auth) -> auth.disable());

		// 로그인, 회원가입은 아무나 가능
		// 나머지 요청은 역할에 따라 나누기
		http
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/login", "/", "/signup").permitAll()
				.requestMatchers("/reissue").permitAll()
				.anyRequest().authenticated());

		http
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),LoginFilter.class);

		http
			.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtTokenProvider,redisService,jwtSettings), UsernamePasswordAuthenticationFilter.class);

		http
			.addFilterBefore(new CustomLogoutFilter(jwtTokenProvider, redisService), LoginFilter.class);

		// JWT를 통한 인증/인가를 위해서 세션을 STATELESS 상태로 설정
		http
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
