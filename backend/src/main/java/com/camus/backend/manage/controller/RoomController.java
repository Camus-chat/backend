package com.camus.backend.manage.controller;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.manage.domain.dto.RoomIdDto;
import com.camus.backend.manage.domain.dto.RoomListDto;
import com.camus.backend.manage.service.RoomService;
import com.camus.backend.manage.util.ChannelStatus;
import com.camus.backend.manage.util.ManageConstants;
import com.camus.backend.manage.util.RoomEntryManager;
import com.camus.backend.member.domain.dto.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/room")
public class RoomController {

	private final RoomService roomService;

	public RoomController(RoomService roomService) {
		this.roomService = roomService;
	}

	@Operation(
		summary = "방 리스트 조회",
		description = "전체 채팅방 리스트를 조회하는 api"
	)
	@GetMapping("/list")
	public ResponseEntity<RoomListDto> getRoomList() {
		// TODO : ROOM 받아오는 로직 구현 -> Redis
		return ResponseEntity.ok(RoomListDto.builder().roomList(new ArrayList<>()).build());
	}

	// FeatureID : 게스트 ROOM 입장하기 & 생성하기

	public ResponseEntity<RoomIdDto> enterRoom(
		// TODO : 사용자 인증 정보
		@RequestBody UUID channelLink
	) {
		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		// CHECK : 여기서 이미 사용자 인증이 되었다고 가정
		//UUID tempMemberId = ManageConstants.tempMemUuid;

		RoomEntryManager roomEntryManager;
		// TODO : 기존에 그 채널에 들어가 있는가? 체크 => 진입
		roomEntryManager = roomService.isChannelMember(userUuid, channelLink);

		if (roomEntryManager.isCheck()) {
			return ResponseEntity.ok(RoomIdDto.builder().roomId(
				roomEntryManager.getRoomId()
			).build());
		}

		ChannelStatus channelStatus = roomService.channelStatus(channelLink);
		// TODO : 채널 링크가 유효한가? 체크 => 진입
		if (!channelStatus.isValid()) {
			throw new CustomException(ErrorCode.NOTFOUND_CHANNEL);
		}

		// TODO : 개인 : 새로운 ROOM 생성 => 진입
		if (channelStatus.getType().equals("private")) {
			UUID newRoomId = new UUID(0, 0);

			// 입장 성공
			return
				ResponseEntity.ok(RoomIdDto.builder().roomId(
					newRoomId
				).build());
		}


		// TODO : 단체 : 기존에 ROOM이 있는가? => 진입

		// TODO : 단체 : 방 생성 및 진입

		UUID tempRoomId = new UUID(0, 0);
		// 입장 성공
		return
			ResponseEntity.ok(RoomIdDto.builder().roomId(
				tempRoomId
			).build());
	}

	// FeatureID : ROOM 채팅 기록 읽어오기

}
