package com.camus.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.camus.backend.global.jwt.util.CustomLogoutFilter;
import com.camus.backend.global.jwt.util.JwtAuthenticationFilter;
import com.camus.backend.global.jwt.util.JwtSettings;
import com.camus.backend.global.jwt.util.JwtTokenProvider;
import com.camus.backend.global.jwt.util.LoginFilter;
import com.camus.backend.global.jwt.service.RedisService;

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
			.csrf(AbstractHttpConfigurer::disable);

		// form 로그인 방식 disable (기본 로그인 창 제거)
		http
			.formLogin((auth) -> auth.disable());

		// http basic 인증 방식 disable (Http basic Auth 기반 로그인 창 제거)
		http
			.httpBasic((auth) -> auth.disable());

		http
			.logout((auth) -> auth.disable());

		// 요청을 역할에 따라 나누기
		http
			.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/").permitAll() // 메인페이지
				.requestMatchers("/member/b2c/login","/member/b2c/signup").permitAll() // b2c 유저 로그인, 회원가입
				.requestMatchers("/member/b2b/login","/member/b2b/signup").permitAll() // b2b 유저 로그인, 회원가입
				.requestMatchers("/guest/login", "/guest/signup").permitAll() // 게스트
				.requestMatchers("/reissue").permitAll() // access 토큰 재발급
				.requestMatchers("/error").permitAll() // 에러 보기
				.anyRequest().authenticated());

		http
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),LoginFilter.class);

		http
			.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),jwtTokenProvider,redisService,jwtSettings), UsernamePasswordAuthenticationFilter.class);

		http
			.addFilterBefore(new CustomLogoutFilter(jwtTokenProvider, redisService), LogoutFilter.class);

		// JWT를 통한 인증/인가를 위해서 세션을 STATELESS 상태로 설정
		http
			.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
