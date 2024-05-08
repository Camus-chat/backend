package com.camus.backend.global.jwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.camus.backend.member.domain.document.MemberCredential;
import com.camus.backend.member.domain.dto.CustomUserDetails;
import com.camus.backend.member.domain.repository.MemberCredentialRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {

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

		// System.out.println(memberCredentialData.get_id());
		// System.out.println(memberCredentialData.getUsername());
		// System.out.println(memberCredentialData.getPassword());
		// System.out.println(memberCredentialData.getRole());

		return new CustomUserDetails(memberCredentialData);
	}
}
