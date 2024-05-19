package com.camus.backend.member.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.member.domain.dto.B2BProfileDto;
import com.camus.backend.member.domain.dto.SignUpDto;
import com.camus.backend.member.domain.dto.UUIDDto;
import com.camus.backend.member.service.MemberService;

@RestController
@RequestMapping("/member/etc")
public class MemberETCController {

	private final MemberService memberService;

	public MemberETCController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping("/check")
	ResponseEntity<?> idCheck(@RequestBody SignUpDto signUpDto){
		return ResponseEntity.ok(memberService.idCheck(signUpDto.getUsername()));
	}

	@PostMapping("/info")
	public ResponseEntity<?> getMemberInfo(@RequestBody UUIDDto uuidDto) {
		return ResponseEntity.ok(memberService.getMemberInfo(uuidDto));
	}



}
