package com.camus.backend.auth.controller;

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

	@PostMapping
	public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

		return reissueService.reissueToken(request, response);
	}
}