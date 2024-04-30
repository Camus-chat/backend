package com.camus.backend.service;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.camus.backend.document.MemberCredential;
import com.camus.backend.dto.MemberCredentialDto;
import com.camus.backend.repository.MemberCredentialRepository;

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

		// 이미 사용되는 id면 생성 못함
		Boolean isExist=memberCredentialRepository.existsByUsername(username);
		if(isExist){
			return false;
		}

		MemberCredential newMemberCredential=MemberCredential.builder()
			._id(UUID.randomUUID())
			.username(username)
			.password(bCryptPasswordEncoder.encode(password))
			.role(role)
			.build();

		memberCredentialRepository.save(newMemberCredential);
		return true;
	}
}
