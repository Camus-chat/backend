package com.camus.backend.member.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.util.SuccessCode;
import com.camus.backend.member.domain.dto.B2CProfileDto;
import com.camus.backend.member.domain.dto.B2CUpdateImageDto;
import com.camus.backend.member.domain.dto.B2CUpdateNicknameDto;
import com.camus.backend.member.domain.dto.MemberCredentialDto;
import com.camus.backend.member.domain.dto.SignUpDto;
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
		memberService.signUp(memberCredentialDto,"b2c");
		// boolean signUpSuccess = memberService.signUp(memberCredentialDto,"b2c");
		// List<String> credentials = memberService.signUp(memberCredentialDto,"b2c");
		// if (!signUpSuccess) {
		// 	return ResponseEntity.badRequest().body("이미 존재하는 사용자 이름입니다.");
		// }
		return ResponseEntity.ok(SuccessCode.SIGNUP);
	}

	// @GetMapping("/info")
	// public ResponseEntity<B2CProfileDto> getMemberInfo() {
	// 	try{
	// 		B2CProfileDto b2cProfileDto = memberService.getB2CInfo();
	// 		return ResponseEntity.ok(b2cProfileDto);
	// 	} catch (CustomException e) {
	// 		return ResponseEntity
	// 			.status(e.getHttpStatusCode())
	// 			.body(e.getErrorCode);
	// 	}
	// }

	@GetMapping("/info")
	public ResponseEntity<?> getMemberInfo() {
		try {
			B2CProfileDto b2cProfileDto = memberService.getB2CInfo();
			return ResponseEntity.ok(b2cProfileDto);
		} catch (CustomException e) {
			// 커스텀 예외를 사용하여 에러코드를 기반으로 에러 응답 생성
			return ResponseEntity
				.status(e.getHttpStatusCode())
				.body(e.getErrorKey());
		}
	}

	@PatchMapping("/image")
	public ResponseEntity<?> changeProfileImage(@ModelAttribute B2CUpdateImageDto b2CUpdateImageDto) {
		try {
			memberService.changeImage(b2CUpdateImageDto);
			return ResponseEntity.ok(SuccessCode.PROFILE_EDIT);
		} catch (CustomException e) {
			return ResponseEntity
				.status(e.getHttpStatusCode())
				.body(e.getErrorKey());
		}
	}

	@PatchMapping("/nickname")
	public ResponseEntity<?> changeProfileNickname(@RequestBody B2CUpdateNicknameDto b2CUpdateNicknameDto) {
		try {
			memberService.changeNickname(b2CUpdateNicknameDto);
			return ResponseEntity.ok(SuccessCode.NICKNAME_EDIT);
		} catch (CustomException e) {
			return ResponseEntity
				.status(e.getHttpStatusCode())
				.body(e.getErrorKey());
		}
	}


}
