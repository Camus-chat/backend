package com.camus.backend.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.member.domain.dto.MemberCredentialDto;
import com.camus.backend.member.service.MemberService;

@RestController
@RequestMapping("/member/b2c")
public class B2CMemberController {

	private final MemberService memberService;

	public B2CMemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping("/signup")
	ResponseEntity<?> b2cSignUp(MemberCredentialDto memberCredentialDto){
		// String role=memberCredentialDto.getRole();
		// boolean signUpSuccess = memberService.signUp(memberCredentialDto,role);
		boolean signUpSuccess = memberService.signUp(memberCredentialDto,"b2c");
		if (!signUpSuccess) {
			return ResponseEntity.badRequest().body("이미 존재하는 사용자 이름입니다.");
		}
		return ResponseEntity.ok("회원가입 성공");
	}
}
