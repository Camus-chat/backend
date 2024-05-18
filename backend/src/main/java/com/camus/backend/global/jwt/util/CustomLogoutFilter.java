package com.camus.backend.global.jwt.util;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.GenericFilterBean;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.global.jwt.service.RedisService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLogoutFilter extends GenericFilterBean {

	// logout 용도
	
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;

	public CustomLogoutFilter(JwtTokenProvider jwtTokenProvider, RedisService redisService) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisService = redisService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		// post로 로그아웃 하면 필터로 감지함
		String requestUri = request.getRequestURI();
		// if (!requestUri.matches("/api/member/logout")) {
		// 	filterChain.doFilter(request, response);
		// 	return;
		// }
		if (!requestUri.matches("^.*/logout$")) {
			filterChain.doFilter(request, response);
			return;
		}
		String requestMethod = request.getMethod();
		if (!requestMethod.equals("POST")) {
			filterChain.doFilter(request, response);
			return;
		}

		//refresh token 가져오기
		String refreshToken=null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("refresh")) {
				refreshToken = cookie.getValue();
			}
		}

		//refresh 토큰이 없음
		if (refreshToken == null) {
			throw new CustomException(ErrorCode.NOTFOUND_TOKEN);
			//response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			//return;
		}

		String accessUsername = jwtTokenProvider.getUsername(refreshToken);

		//refresh 만료 확인
		try {
			jwtTokenProvider.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.FORBIDDEN_TOKEN_EXPIRED);
			// response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			// return;
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtTokenProvider.getCategory(refreshToken);
		if (!category.equals("refresh")) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_TOKEN);
			// response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			// return;
		}

		// redis에 refresh가 저장되어 있는지 확인
		if (!redisService.doesRefreshTokenNotExist(accessUsername)) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_TOKEN);
			// response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			// return;
		}

		//로그아웃 진행
		//Refresh 토큰 레디스에서 제거
		redisService.deleteRefreshToken(accessUsername);

		//Refresh 토큰 Cookie 값 0으로 바꿔주기
		Cookie cookie = new Cookie("refresh", null);
		cookie.setMaxAge(0);
		// cookie.setPath("/");

		response.addCookie(cookie);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"message\": \"로그아웃 성공\"}");
	}
}
