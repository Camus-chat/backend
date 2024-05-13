package com.camus.backend.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.global.util.SuccessCode;
import com.camus.backend.member.domain.dto.MemberCredentialDto;
import com.camus.backend.member.domain.dto.SignUpDto;
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
		memberService.signUp(memberCredentialDto,"b2b");
		// boolean signUpSuccess = memberService.signUp(memberCredentialDto,"b2b");
		// List<String> credentials = memberService.signUp(memberCredentialDto,"b2b");
		// if (!signUpSuccess) {
		// 	return ResponseEntity.badRequest().body("이미 존재하는 사용자 이름입니다.");
		// }
		return ResponseEntity.ok(SuccessCode.SIGNUP);
	}

	// @PostMapping("/check")
	// ResponseEntity<?> b2bIdCheck(@RequestBody SignUpDto signUpDto){
	// 	return ResponseEntity.ok(memberService.idCheck(signUpDto.getUsername()));
	// }

}
