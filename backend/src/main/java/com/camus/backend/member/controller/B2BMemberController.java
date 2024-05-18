package com.camus.backend.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.util.SuccessCode;
import com.camus.backend.member.domain.dto.B2BMemberCredentialDto;
import com.camus.backend.member.domain.dto.B2BProfileDto;
import com.camus.backend.member.domain.dto.B2BUpdateDto;
import com.camus.backend.member.service.MemberService;

@RestController
@RequestMapping("/member/b2b")
public class B2BMemberController {

	private final MemberService memberService;

	public B2BMemberController(MemberService memberService) {

		this.memberService = memberService;
	}

	@PostMapping("/signup")
	ResponseEntity<?> b2bSignUp(@RequestBody B2BMemberCredentialDto b2bMemberCredentialDto){
		// String role=memberCredentialDto.getRole();
		memberService.b2bSignUp(b2bMemberCredentialDto,"b2b");
		// boolean signUpSuccess = memberService.signUp(memberCredentialDto,"b2b");
		// List<String> credentials = memberService.signUp(memberCredentialDto,"b2b");
		// if (!signUpSuccess) {
		// 	return ResponseEntity.badRequest().body("이미 존재하는 사용자 이름입니다.");
		// }
		return ResponseEntity.ok(SuccessCode.SIGNUP);
	}

	// @PostMapping("/signup")
	// ResponseEntity<?> b2bSignUp(MemberCredentialDto memberCredentialDto){
	// 	// String role=memberCredentialDto.getRole();
	// 	memberService.signUp(memberCredentialDto,"b2b");
	// 	// boolean signUpSuccess = memberService.signUp(memberCredentialDto,"b2b");
	// 	// List<String> credentials = memberService.signUp(memberCredentialDto,"b2b");
	// 	// if (!signUpSuccess) {
	// 	// 	return ResponseEntity.badRequest().body("이미 존재하는 사용자 이름입니다.");
	// 	// }
	// 	return ResponseEntity.ok(SuccessCode.SIGNUP);
	// }

	@GetMapping("/info")
	public ResponseEntity<?> getB2BMemberInfo() {
		try {
			B2BProfileDto b2bProfileDto = memberService.getB2BInfo();
			return ResponseEntity.ok(b2bProfileDto);
		} catch (CustomException e) {
			// 커스텀 예외를 사용하여 에러코드를 기반으로 에러 응답 생성
			return ResponseEntity
				.status(e.getHttpStatusCode())
				.body(e.getErrorKey());
		}
	}

	@PatchMapping("/modify")
	public ResponseEntity<?> changeB2BInfo(@RequestBody B2BUpdateDto b2bUpdateDto) {
		try {
			memberService.changeB2BInfo(b2bUpdateDto);
			return ResponseEntity.ok(SuccessCode.MODIFY);
		} catch (CustomException e) {
			return ResponseEntity
				.status(e.getHttpStatusCode())
				.body(e.getErrorKey());
		}
	}

	// @PostMapping("/check")
	// ResponseEntity<?> b2bIdCheck(@RequestBody SignUpDto signUpDto){
	// 	return ResponseEntity.ok(memberService.idCheck(signUpDto.getUsername()));
	// }

}
