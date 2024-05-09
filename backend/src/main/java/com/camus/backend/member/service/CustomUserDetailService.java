package com.camus.backend.member.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.camus.backend.member.domain.document.MemberCredential;
import com.camus.backend.member.domain.dto.CustomUserDetails;
import com.camus.backend.member.domain.repository.MemberCredentialRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

	// db에서 username을 가지고 UserDetails를 반환하는 UserDetailsService 구현

	private final MemberCredentialRepository memberCredentialRepository;

	public CustomUserDetailService(MemberCredentialRepository memberCredentialRepository) {
		this.memberCredentialRepository = memberCredentialRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//DB에서 조회
		MemberCredential memberCredentialData = memberCredentialRepository.findByUsername(username);

		if (memberCredentialData == null) {
			System.out.println("유저를 찾을 수 없음!");
			throw new UsernameNotFoundException("유저를 찾을 수 없음!!!!!! " + username);
		}

		return new CustomUserDetails(memberCredentialData);
	}
}
