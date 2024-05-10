package com.camus.backend.member.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

	// 회원가입(db에 넣기) 후 프론트에 아이디, 비번 보내기
	public List<String> signUp(MemberCredentialDto memberCredentialDto, String role){
		String username= memberCredentialDto.getUsername();
		String password= memberCredentialDto.getPassword();

		// 이미 사용되는 username이면 생성 못함
		Boolean isExist=memberCredentialRepository.existsByUsername(username);
		if(isExist){
			return new ArrayList<>();
		}

		// 비밀번호 암호화
		String encodedPassword = bCryptPasswordEncoder.encode(password);

		MemberCredential newMemberCredential=MemberCredential.builder()
			._id(UUID.randomUUID())
			.username(username)
			.password(encodedPassword)
			.role(role)
			.loginTime(LocalDateTime.now())
			.build();

		memberCredentialRepository.save(newMemberCredential);

		// List<String> credentials = new ArrayList<>();
		// credentials.add(username);
		// credentials.add(password);

		// 시간은 한국 시간으로 정상적으로 찍히네
		// System.out.println(memberCredentialRepository.findByUsername(username).getLoginTime());

		// 저장 후 username과 암호화된 password를 리스트로 반환
		return List.of(username,password);
	}
}
