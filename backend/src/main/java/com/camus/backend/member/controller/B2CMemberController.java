package com.camus.backend.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.member.domain.document.MemberCredential;
import com.camus.backend.member.domain.dto.B2CProfileDto;
import com.camus.backend.member.domain.dto.LoginDto;
import com.camus.backend.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/member/b2c")
public class B2CMemberController {

	private final MemberService service;

	@Autowired
	public B2CMemberController(MemberService service) {
		this.service = service;
	}

	// FeatureID 101-1
	// TODO : token 인증 과정 & 아이디 유효성/비밀번호 유효 검사
	@Operation(
		summary = "B2C 회원가입",
		description = "B2C 회원가입 - 아이디/비밀번호"
	)
	@PostMapping("/signup")
	public ResponseEntity<MemberCredential> createMemberCredential(@RequestBody MemberCredential credentials) {
		MemberCredential savedCredentials = service.save(credentials);
		return ResponseEntity.ok(savedCredentials);
	}

	// FeatureID 102-1
	@Operation(
		summary = "B2C 로그인",
		description = "B2C 로그인 기능 - 아이디/비밀번호"
	)
	@PostMapping("/login")
	public ResponseEntity<LoginDto> login(
		// TODO : token인증 및 정보 찾는 과정에 대한 로직
	) {

		// TODO : authorization에 token넣기
		return ResponseEntity.ok(LoginDto.builder().authorization("authorization").build());
	}

	// FeatureID 104-1
	@Operation(
		summary = "B2C 회원정보 조회",
		description = "B2C 프로필 이미지, 닉네임 요청"
	)
	@GetMapping("/info")
	public ResponseEntity<B2CProfileDto> getProfileBySelf(
		// TODO : @AuthenticationPrincipal 멤버 token에서 uuid 받기
	) {

		return ResponseEntity.ok(service.readMemberBySelf(
			// TODO : 인증된 사용자 객체 줘야됨.
		));
	}

	// @Operation(
	// 	summary = "B2C 회원정보 수정",
	// 	description = "B2C 프로필 이미지 링크, 닉네임 변경"
	// )
	// // CHECK : modify말고 edit 쓰면 안돼?ㅠ
	// @PostMapping("/modify")
	// public ResponseEntity<B2CProfileEditDto> editProfileBySelf(
	// 	@RequestBody B2CProfileEditRequestDto profileEditRequestDto,
	// 	// TODO : @AuthenticationPrincipal 멤버 token에서 uuid 받기
	// ) {
	// 	return ResponseEntity.ok(service.EditProfileBySelf());
	// }

}

