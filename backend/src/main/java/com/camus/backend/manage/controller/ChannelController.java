package com.camus.backend.manage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.manage.domain.dto.ChannelDto;
import com.camus.backend.manage.domain.dto.CreateChannelDto;
import com.camus.backend.manage.service.ChannelService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/channel")
public class ChannelController {

	private final ChannelService channelService;

	public ChannelController(ChannelService channelService) {
		this.channelService = channelService;
	}

	@Operation(
		summary = "채널 생성",
		description = "채널 생성을 위한 api입니다. 채널"
	)
	@PostMapping("/create")
	public ResponseEntity<ChannelDto> createChannel(
		@RequestBody CreateChannelDto channelDto
		// TODO : 사용자 인증 정보 - 회원일 때만
	) {
		// TODO : 사용자 정보 삭제하고 인증에서 받아오기
		String uuid = "0000";

		return ResponseEntity.ok(channelService.createChannel(channelDto
			// TODO : 여기서 사용자 정보 넘기기
		));
	}

}
