package com.camus.backend.member.controller;

import static com.camus.backend.global.util.GuestUtil.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.member.domain.dto.MemberCredentialDto;
import com.camus.backend.member.service.MemberService;

@RestController
@RequestMapping("/guest")
public class GuestController {

	private final MemberService memberService;

	public GuestController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping("/signup")
	ResponseEntity<?> guestSignUp(){
		// String role=memberCredentialDto.getRole();
		// boolean signUpSuccess = memberService.signUp(memberCredentialDto,role);

		// memberCredentialDto 새로 생성
		MemberCredentialDto memberCredentialDto = MemberCredentialDto.builder()
			.username(generateUsername())
			.password("guestPwd")
			.build();

		boolean signUpSuccess = memberService.signUp(memberCredentialDto,"guest");
		if (!signUpSuccess) {
			return ResponseEntity.badRequest().body("게스트 생성 에러입니다.");
		}
		return ResponseEntity.ok("회원가입 성공");
	}
	
}
