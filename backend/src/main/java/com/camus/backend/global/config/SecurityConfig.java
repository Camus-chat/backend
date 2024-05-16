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
import com.camus.backend.member.domain.repository.MemberCredentialRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	//AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
	private final AuthenticationConfiguration authenticationConfiguration;

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;
	private final JwtSettings jwtSettings;
	private final MemberCredentialRepository memberCredentialRepository;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JwtTokenProvider jwtTokenProvider,
		RedisService redisService, JwtSettings jwtSettings, MemberCredentialRepository memberCredentialRepository) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisService = redisService;
		this.jwtSettings = jwtSettings;
		this.memberCredentialRepository = memberCredentialRepository;
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
				.requestMatchers("/swagger","/swagger-ui.html","/swagger-ui/**","/v3/api-docs/**").permitAll() // swagger 설정
				.requestMatchers("/").permitAll() // 메인페이지
				.requestMatchers("/member/b2c/login","/member/b2c/signup").permitAll() // b2c 유저 로그인, 회원가입
				.requestMatchers("/member/b2b/login","/member/b2b/signup").permitAll() // b2b 유저 로그인, 회원가입
				.requestMatchers("/guest/login", "/guest/signup").permitAll() // 게스트
				.requestMatchers("/reissue").permitAll() // access 토큰 재발급
				.requestMatchers("/check").permitAll() // id 중복체크
				.requestMatchers("/error").permitAll() // 에러 보기
				.requestMatchers("/member/b2c/info", "/member/b2c/image", "/member/b2c/nickname").hasAuthority("b2c") // b2c 회원정보 조회, 이미지 수정, 닉네임 수정
				.requestMatchers("/member/b2b/info", "/member/b2b/modify").hasAuthority("b2b") // b2b 회원정보 조회, 회원정보 수정
				.requestMatchers("/chat/data/unread", "/chat/room/exit", "/chat/room/data").permitAll() // ChatDataController
				.requestMatchers("/test/redisCreateRoomNoticeTest", "/test/redisSendMessagesTest").permitAll() // TestController
				.requestMatchers("/channel/tempSave", "/channel/create", "/channel/list", "/channel/disable", "/channel/edit").permitAll() // ChannelController
				.requestMatchers("/room/list", "/room/guest/enter").permitAll() // RoomController
				.requestMatchers("/model/clova", "/model/lambda").permitAll() // ModelTestController
				.requestMatchers("/statistic/member", "/statistic/channel").permitAll() // StatisticController
				.requestMatchers("/filter/clova", "/filter/lambda",  "/filter/bad").permitAll() // FilterTestController
				// permitall이나 hasrole로 라이브러리화 할때 쓰기
				.anyRequest().authenticated());

		http
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, memberCredentialRepository),LoginFilter.class);

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
