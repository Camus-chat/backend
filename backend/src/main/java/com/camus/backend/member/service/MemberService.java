package com.camus.backend.member.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.camus.backend.member.domain.document.MemberCredential;
import com.camus.backend.member.domain.dto.MemberCredentialDto;
import com.camus.backend.member.domain.repository.MemberCredentialRepository;

@Service
public class MemberService {

	private final MemberCredentialRepository memberCredentialRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public MemberService(MemberCredentialRepository memberCredentialRepository,
		BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.memberCredentialRepository = memberCredentialRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	// 회원가입 시 역할 주기
	public boolean signUp(MemberCredentialDto memberCredentialDto, String role){
		String username= memberCredentialDto.getUsername();
		String password= memberCredentialDto.getPassword();

		// 이미 사용되는 username이면 생성 못함
		Boolean isExist=memberCredentialRepository.existsByUsername(username);
		if(isExist){
			return false;
		}

		MemberCredential newMemberCredential=MemberCredential.builder()
			._id(UUID.randomUUID())
			.username(username)
			.password(bCryptPasswordEncoder.encode(password))
			.role(role)
			.loginTime(LocalDateTime.now())
			.build();

		memberCredentialRepository.save(newMemberCredential);

		// 시간은 한국 시간으로 정상적으로 찍히네
		// System.out.println(memberCredentialRepository.findByUsername(username).getLoginTime());

		return true;
	}
}
