package com.camus.backend.global.jwt.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.member.domain.document.MemberCredential;
import com.camus.backend.member.domain.dto.CustomUserDetails;
import com.camus.backend.member.domain.repository.MemberCredentialRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	// jwtfilter에 해당
	// jwt 검증함

	private final JwtTokenProvider jwtTokenProvider;
	private final MemberCredentialRepository memberCredentialRepository;

	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
		MemberCredentialRepository memberCredentialRepository){
		this.jwtTokenProvider=jwtTokenProvider;
		this.memberCredentialRepository = memberCredentialRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException, IOException {

		// 헤더에서 access키에 담긴 토큰을 꺼냄
		String accessToken = request.getHeader("access");

		// 토큰이 없다면 다음 필터로 넘김
		if (accessToken == null) {
			filterChain.doFilter(request, response);
			return;
		}

		// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
		try {
			jwtTokenProvider.isExpired(accessToken);
		} catch (ExpiredJwtException e) {
			//response body
			PrintWriter writer = response.getWriter();
			writer.print("access token 만료됨");

			throw new CustomException(ErrorCode.FORBIDDEN_TOKEN_EXPIRED);
			// //response status code
			// response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			// return;
		}

		// 토큰이 access인지 확인 (발급시 페이로드에 명시)
		String category = jwtTokenProvider.getCategory(accessToken);
		if (!category.equals("access")) {
			//response body
			PrintWriter writer = response.getWriter();
			writer.print("유효하지 않은 access token");
			throw new CustomException(ErrorCode.INVALID_PARAMETER_TOKEN);
			// //response status code
			// response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			// return;
		}

		// username, role 값을 획득
		String username = jwtTokenProvider.getUsername(accessToken);
		String role = jwtTokenProvider.getRole(accessToken);
		UUID uuid = memberCredentialRepository.findByUsername(username).get_id();

		// memberCredential를 생성하여 값 set
		// 세션 처리를 위한 임시객체
		MemberCredential memberCredential = MemberCredential.builder()
			._id(uuid)
			.username(username)
			.role(role)
			.build();

		//UserDetails에 회원 정보 객체 담기
		CustomUserDetails customUserDetails = new CustomUserDetails(memberCredential);

		//스프링 시큐리티 인증 토큰 생성
		Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
			customUserDetails.getAuthorities());
		//세션에 사용자 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}
}
