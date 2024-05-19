package com.camus.backend.manage.controller;

import java.util.List;
import java.util.UUID;

import com.camus.backend.manage.domain.document.Room;
import com.camus.backend.manage.domain.dto.RoomEnterDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.manage.domain.dto.RoomDto;
import com.camus.backend.manage.domain.dto.RoomIdDto;
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
	public ResponseEntity<List<RoomDto>> getRoomList(
		// 사용자 정보 받기
	) {

		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		// 여기는 게스트유저~~~임
		// UUID tempMemberId = ManageConstants.tempMemUuid;

		return ResponseEntity.ok(roomService.getRoomListByOwnerId(userUuid));
	}

	// FeatureID : 게스트 ROOM 입장하기 & 생성하기
	@Operation(
		summary = "게스트가 링크로 진입시 방 입장하기",
		description = "기존방/신규(개인/그룹)방 모두 동일처리"
	)
	@PostMapping("/guest/enter")
	public ResponseEntity<RoomEnterDto> enterRoom(
		// TODO : 사용자 인증 정보
		@RequestBody UUID channelLink
	) {
		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		// CHECK : 여기서 이미 사용자 인증이 되었다고 가정
		//UUID tempMemberId = ManageConstants.tempMemUuid;

		ChannelStatus channelStatus = roomService.channelStatus(channelLink);

		RoomEntryManager roomEntryManager;
		// TODO : 기존에 그 채널에 들어가 있는가? 체크 => 진입
		roomEntryManager = roomService.isChannelMember(userUuid, channelLink);



		if (roomEntryManager.isCheck()) {
			Room room = roomService.getRoomByRoomId(roomEntryManager.getRoomId());
			return ResponseEntity.ok(
				RoomEnterDto.builder()
						.roomId(roomEntryManager.getRoomId())
						.channelType(channelStatus.getType())
						.channelTitle(channelStatus.getTitle())
						.filteredLevel(channelStatus.getFilteredLevel())
						.memberList(room.getUserList())
						.isClosed(room.isClosed())
						.build()
			);
		}



		// TODO : 채널 링크가 유효한가? 체크 => 진입
		if (!channelStatus.isValid()) {
			throw new CustomException(ErrorCode.NOTFOUND_CHANNEL);
		}

		// TODO : 개인 : 새로운 ROOM 생성 => 진입
		if (channelStatus.getType().equals("private")) {

			UUID roomId = roomService.createPrivateRoomByGuestId(
				channelStatus.getKey(),
				channelStatus.getOwnerId(), userUuid
			);
			Room room = roomService.getRoomByRoomId(roomId);
			// 입장 성공
			return
				ResponseEntity.ok(
					RoomEnterDto.builder()
						.roomId(roomId)
						.channelType(channelStatus.getType())
						.channelTitle(channelStatus.getTitle())
						.filteredLevel(channelStatus.getFilteredLevel())
						.memberList(room.getUserList())
						.isClosed(room.isClosed())
						.build()
				);
		}

		UUID roomId = roomService.joinGroupRoom(channelStatus.getKey(), userUuid);
		Room room = roomService.getRoomByRoomId(roomId);

		// TODO : 단체 : 기존 ROOM에 입장
		return
			ResponseEntity.ok(
					RoomEnterDto.builder()
							.roomId(roomId)
							.channelType(channelStatus.getType())
							.channelTitle(channelStatus.getTitle())
							.filteredLevel(channelStatus.getFilteredLevel())
							.memberList(room.getUserList())
							.isClosed(room.isClosed())
							.build()
			);
	}

}
