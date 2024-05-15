package com.camus.backend.global.jwt.util;

import java.util.Arrays;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	// UsernamePasswordAuthenticationFilter 커스터마이징해서 login 경로 바꾸기
	
	public CustomUsernamePasswordAuthenticationFilter() {
		// RequestMatcher를 설정하고 AuthenticationManager를 수동으로 설정
		setRequiresAuthenticationRequestMatcher(new OrRequestMatcher(
			Arrays.asList(
				new AntPathRequestMatcher("/member/b2c/login", "POST"),
				new AntPathRequestMatcher("/member/b2b/login", "POST")
				// 게스트는 따로
				//new AntPathRequestMatcher("/guest/login", "POST")
			)
		));
	}

}
