package com.camus.backend.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.global.util.SuccessResponseDto;
import com.camus.backend.member.domain.document.MemberCredential;
import com.camus.backend.member.domain.dto.B2CProfileDto;
import com.camus.backend.member.domain.dto.B2CUpdateNicknameDto;
import com.camus.backend.member.domain.dto.TokenDto;
import com.camus.backend.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/member/b2c")
public class B2CMemberController {

	private final MemberService service;

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
		// CHECK : 회원가입 시 어떤 로직으로 Token을 발급하는가?
		MemberCredential savedCredentials = service.save(credentials);
		return ResponseEntity.ok(savedCredentials);
	}

	// FeatureID 102-1
	@Operation(
		summary = "B2C 로그인",
		description = "B2C 로그인 기능 - 아이디/비밀번호"
	)
	@PostMapping("/login")
	public ResponseEntity<TokenDto> login(
		// TODO : 인증 정보 알아서 처리하고 사용자 객체 여기서 받기
	) {

		// TODO : authorization에 token넣기
		// TODO : service 단에서 token 발급 로직 추가
		return ResponseEntity.ok(TokenDto.builder().authorization("token").build());
	}

	// FeatureID 104-1
	@Operation(
		summary = "B2C 회원정보 조회",
		description = "B2C 프로필 이미지, 닉네임 요청"
	)
	@GetMapping("/profile")
	public ResponseEntity<B2CProfileDto> getProfileBySelf(
		// TODO : @AuthenticationPrincipal 멤버 token에서 uuid 받기
	) {

		return ResponseEntity.ok(service.readMemberBySelf(
			// TODO : 인증된 사용자 객체 줘야됨.
		));
	}

	// FeatureID 105-1
	@Operation(
		summary = "B2C 회원 닉네임 수정",
		description = "B2C 닉네임 변경"
	)
	@PatchMapping("/update/nickname")
	public ResponseEntity<SuccessResponseDto> editNicknameBySelf(
		@RequestBody B2CUpdateNicknameDto b2CUpdateNicknameDto
		// TODO : @AuthenticationPrincipal 멤버 token에서 uuid 받기
	) {

		return ResponseEntity.ok(service.updateNicknameBySelf(
			// TODO :  인증클래스 전달
			b2CUpdateNicknameDto
		));
	}

	// FeatureID 105-1
	@Operation(
		summary = "B2C 회원 사진 업로드 - 링크 수정",
		description = "B2C 회원 프로필 이미지 변경"
	)
	@PostMapping("/update/image")
	public ResponseEntity<SuccessResponseDto> editImageBySelf(

	) {
		// TODO : @AuthenticationPrincipal 멤버 token에서 uuid 받기
		// TODO : 이미지 업로드 로직
		return ResponseEntity.ok(
			// TODO : 처리 후 응답
			SuccessResponseDto.builder().build()
		);
	}

}

