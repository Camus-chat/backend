package com.camus.backend.member.controller;

import static com.camus.backend.global.util.GuestUtil.*;

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

		memberService.signUp(memberCredentialDto,"guest");
		// List<String> credentials = memberService.signUp(memberCredentialDto,"guest");
		// if (credentials.isEmpty()) {
		// 	return ResponseEntity.badRequest().body("게스트 생성 에러입니다.");
		// }
		return ResponseEntity.ok(SuccessCode.SIGNUP);
	}

	@PostMapping("/check")
	ResponseEntity<?> b2cIdCheck(@RequestBody SignUpDto signUpDto){
		return ResponseEntity.ok(memberService.idCheck(signUpDto.getUsername()));
	}
}
