package com.camus.backend.jwt.util;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.camus.backend.jwt.service.RedisService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomLogoutFilter extends GenericFilterBean {

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
		if (!requestUri.matches("^\\/logout$")) {
			filterChain.doFilter(request, response);
			return;
		}
		String requestMethod = request.getMethod();
		if (!requestMethod.equals("POST")) {
			filterChain.doFilter(request, response);
			return;
		}

		//refresh token 가져오기
		String accessToken = request.getHeader("access");
		String accessUsername = jwtTokenProvider.getUsername(accessToken);
		String refresh = redisService.getRefreshToken(accessUsername);

		//refresh 토큰이 없음
		if (refresh == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//refresh 만료 확인
		try {
			jwtTokenProvider.isExpired(refresh);
		} catch (ExpiredJwtException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtTokenProvider.getCategory(refresh);
		if (!category.equals("refresh")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//로그아웃 진행
		//Refresh 토큰 레디스에서 제거
		redisService.deleteRefreshToken(accessUsername);

		response.setStatus(HttpServletResponse.SC_OK);
	}
}
