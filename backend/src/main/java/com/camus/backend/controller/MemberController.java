package com.camus.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.dto.MemberCredentialDto;
import com.camus.backend.service.MemberService;

@RestController("/member")
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping("/signup")
	ResponseEntity<?> signUp(MemberCredentialDto memberCredentialDto){
		String role=memberCredentialDto.getRole();
		boolean signUpSuccess = memberService.signUp(memberCredentialDto,role);
		if (!signUpSuccess) {
			return ResponseEntity.badRequest().body("이미 존재하는 사용자 이름입니다.");
		}
		return ResponseEntity.ok("회원가입 성공");
	}
}
