package com.camus.backend.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.member.domain.dto.MemberCredentialDto;
import com.camus.backend.member.service.MemberService;

@RestController
@RequestMapping("/member/b2b")
public class B2BMemberController {

	private final MemberService memberService;

	public B2BMemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping("/signup")
	ResponseEntity<?> b2bSignUp(MemberCredentialDto memberCredentialDto){
		// String role=memberCredentialDto.getRole();
		// boolean signUpSuccess = memberService.signUp(memberCredentialDto,role);
		boolean signUpSuccess = memberService.signUp(memberCredentialDto,"b2b");
		if (!signUpSuccess) {
			return ResponseEntity.badRequest().body("이미 존재하는 사용자 이름입니다.");
		}

		return ResponseEntity.ok("회원가입 성공");
	}
}
