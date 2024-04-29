package com.camus.backend.manage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.global.util.SuccessCode;
import com.camus.backend.manage.domain.dto.ChannelDto;
import com.camus.backend.manage.domain.dto.ChannelListDto;
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

	// FIXME : 지워주세요 tempSave
	@PostMapping("/tempSave")
	public ResponseEntity<SuccessCode> tempSave() {
		channelService.createChannelList();
		// TODO : 임시 저장 기능 구현
		return ResponseEntity.ok(SuccessCode.CHANNEL_EDIT);
	}

	// FeatureID 501-1
	@Operation(
		summary = "채널 생성",
		description = "채널 생성을 위한 api입니다. 채널"
	)
	@PostMapping("/create")
	public ResponseEntity<ChannelDto> createChannel(
		@RequestBody CreateChannelDto channelDto
		// TODO : 사용자 인증 정보 - 회원일 때만
	) {

		return ResponseEntity.ok(channelService.createChannel(channelDto
			// TODO : 여기서 사용자 정보 넘기기
		));
	}

	// FeatureID 501-1
	@Operation(
		summary = "채널 리스트 반환",
		description = "채널 리스트를 반환합니다."
	)
	@GetMapping("/list")
	public ResponseEntity<ChannelListDto> getChannelList() {

		return ResponseEntity.ok(channelService.getChannelList(
			// TODO : 여기서 사용자 정보 넘기기
		));
	}

	// FeatureID 503-1
	@Operation(
		summary = "채널 링크 비활성화",
		description = "채널 링크를 비활성화합니다."
	)
	@PatchMapping("/disable")
	public ResponseEntity<SuccessCode> disableChannel(
		String channelLink
	) {
		// TODO : 사용자 정보 삭제하고 인증에서 받아오기

		channelService.disableChannel(
			// TODO : 여기서 사용자 정보 넘기기
			channelLink
		);

		return ResponseEntity.ok(SuccessCode.CHANNEL_DISABLE);
	}

	// FeatureID 510-1
	@Operation(
		summary = "채널 제목 변경",
		description = "채널 제목 변경"
	)
	@PatchMapping("/update/title")
	public ResponseEntity<SuccessCode> updateChannel() {

		//  TODO : 채널 링크 수정 방식 확인하고 메소드 구현
		return ResponseEntity.ok(SuccessCode.CHANNEL_EDIT);
	}
}
