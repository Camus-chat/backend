package com.camus.backend.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.auth.service.ReissueService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/reissue")
public class ReissueController {
	private final ReissueService reissueService;

	public ReissueController(ReissueService reissueService) {
		this.reissueService = reissueService;
	}

	@Operation(
			summary = "액세스 토큰 재발급",
			description = "리프레쉬 토큰을 확인하고 액세스 토큰을 재발급" +
					"\n성공시 {role:string}을 반환" + 
					"\n작동 확인 필요"
	)
	@PostMapping
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

		return reissueService.reissueToken(request, response);
	}
}