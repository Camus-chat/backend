package com.camus.backend.manage.controller;

import java.util.List;
import java.util.UUID;

import com.camus.backend.manage.domain.dto.RoomDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.manage.domain.dto.RoomIdDto;
import com.camus.backend.manage.service.RoomService;
import com.camus.backend.manage.util.ChannelStatus;
import com.camus.backend.manage.util.ManageConstants;
import com.camus.backend.manage.util.RoomEntryManager;

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
	@PostMapping("/list")
	public ResponseEntity<List<RoomDto>> getRoomList(
			// 사용자 정보 받기
	) {
		// 여기는 게스트유저~~~임
		UUID tempMemberId = ManageConstants.tempMemUuid;

		return ResponseEntity.ok(roomService.getRoomListByOwnerId(tempMemberId));
	}

	// FeatureID : 게스트 ROOM 입장하기 & 생성하기
	@Operation(
			summary = "게스트가 링크로 진입시 방 입장하기",
			description = "기존방/신규(개인/그룹)방 모두 동일처리"
	)
	@PostMapping("/guest/enter")
	public ResponseEntity<RoomIdDto> enterRoom(
		// TODO : 사용자 인증 정보
		@RequestBody UUID channelLink
	) {
		// CHECK : 여기서 이미 사용자 인증이 되었다고 가정
		UUID tempMemberId = new UUID(0, 0);

		RoomEntryManager roomEntryManager;
		// TODO : 기존에 그 채널에 들어가 있는가? 체크 => 진입
		// 완료
		roomEntryManager = roomService.isChannelMember(tempMemberId, channelLink);



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

			// 입장 성공
			return
				ResponseEntity.ok(RoomIdDto.builder().roomId(
					roomService.createPrivateRoomByGuestId(
							channelStatus.getKey(),
							channelStatus.getOwnerId(), tempMemberId)
				).build());
		}

		// TODO : 단체 : 기존 ROOM에 입장
		return
			ResponseEntity.ok(RoomIdDto.builder().roomId(
				roomService.joinGroupRoom(channelStatus.getKey(), tempMemberId)
			).build());
	}

}
