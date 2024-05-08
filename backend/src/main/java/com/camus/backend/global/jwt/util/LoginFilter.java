package com.camus.backend.global.jwt.util;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.camus.backend.global.jwt.service.RedisService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	// 로그인 하는 부분
	
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;

	private final JwtSettings jwtSettings;

	public LoginFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
		RedisService redisService, JwtSettings jwtSettings) {
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisService = redisService;
		this.jwtSettings = jwtSettings;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		//클라이언트 요청에서 username, password 추출
		String username = obtainUsername(request);
		String password = obtainPassword(request);

		System.out.println(username+" 비비상");

		//스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

		//token에 담은 검증을 위한 AuthenticationManager로 전달
		return authenticationManager.authenticate(authToken);
	}

	//로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
		String username=authentication.getName();

		System.out.println(username+" 비상");

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		String accessToken = jwtTokenProvider.createToken("access",username,role, jwtSettings.getAccessExpire());
		String refreshToken;
		long cookieRefresh;

		// 회원과 비회원에 따라 refresh 따로 주기
		if(role.equals("guest")){
			cookieRefresh=jwtSettings.getGuestExpire();
			refreshToken = jwtTokenProvider.createToken("refresh",username,role, cookieRefresh);
		}else{
			cookieRefresh=jwtSettings.getRefreshExpire();
			refreshToken = jwtTokenProvider.createToken("refresh",username,role, cookieRefresh);
		}

		// redis에 refresh token 저장
		redisService.storeRefreshToken(username, refreshToken, cookieRefresh);

		// 응답
		response.setHeader("access", accessToken);
		response.addCookie(createCookie("refresh", refreshToken,cookieRefresh));
		response.setStatus(HttpStatus.OK.value());
	}

	//로그인 실패시 실행하는 메소드
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
		//로그인 실패시 401 응답 코드 반환
		response.setStatus(401);
	}

	private Cookie createCookie(String key, String value, long cookieRefreshTime) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge((int)cookieRefreshTime);
		//cookie.setSecure(true); https 통신할거면 넣기
		//cookie.setPath("/");
		cookie.setHttpOnly(true);
		return cookie;
	}

}
