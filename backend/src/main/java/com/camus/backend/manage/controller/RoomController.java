package com.camus.backend.manage.controller;

import java.util.List;
import java.util.UUID;

import com.camus.backend.manage.domain.dto.LinkRoomDto;
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
import com.camus.backend.manage.domain.document.Room;
import com.camus.backend.manage.domain.dto.RoomDto;
import com.camus.backend.manage.domain.dto.RoomEnterDto;
import com.camus.backend.manage.service.RoomService;
import com.camus.backend.manage.util.ChannelStatus;
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
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		// 여기는 게스트유저~~~임
		// UUID tempMemberId = ManageConstants.tempMemUuid;

		return ResponseEntity.ok(roomService.getRoomListByOwnerId(userUuid));
	}

	// FeatureID : 게스트 ROOM 입장하기 & 생성하기
	@Operation(
		summary = "링크로 진입시 방 입장하기",
		description = "기존방/신규(개인/그룹)방 모두 동일처리"+
			"채널 주인 처리 확인 필요"
	)
	@PostMapping("/enter")
	public ResponseEntity<UUID> enterRoom(
		// TODO : 사용자 인증 정보
		@RequestBody UUID channelLink
	) {
		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		ChannelStatus channelStatus = roomService.channelStatus(channelLink);

		// TODO : 채널 링크가 유효한가? 체크 => 진입
		if (!channelStatus.isValid()) {
			throw new CustomException(ErrorCode.NOTFOUND_CHANNEL);
		}

		if (userUuid.equals(channelStatus.getOwnerId())){
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		// TODO : 기존에 그 채널에 들어가 있는가? 체크 => 진입
		RoomEntryManager roomEntryManager = roomService.isChannelMember(userUuid, channelLink);

		if (roomEntryManager.isCheck()) {
			UUID roomId = roomEntryManager.getRoomId();
			System.out.println("room 재진입, roomId: " + roomId);
			return ResponseEntity.ok(roomId);
		}

		// TODO : 개인 : 새로운 ROOM 생성 => 진입
		if (channelStatus.getType().equals("private")) {
			UUID roomId = roomService.createPrivateRoomByGuestId(
				channelStatus.getKey(),
				channelStatus.getOwnerId(), userUuid
			);
			System.out.println("private room 생성 및 진입, roomId: "+roomId);
			return ResponseEntity.ok(roomId);
		}

		// TODO : 단체 : 기존 ROOM 입장
		UUID roomId = roomService.joinGroupRoom(channelStatus.getKey(), userUuid);
		System.out.println("group room 진입, roomId: "+roomId);

		return ResponseEntity.ok(roomId);
		// TODO : 비정상적인 channelType 처리
	}

	// FeatureID : 게스트 ROOM 입장하기 & 생성하기
	@Operation(
			summary = "방 정보 조회",
			description = "대상이 해당 방에 진입한 유저가 방 정보 조회"
	)
	@PostMapping("/info")
	public ResponseEntity<RoomEnterDto> getRoomInfo(
			@RequestBody LinkRoomDto linkRoomDto
			) {
		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();


		ChannelStatus channelStatus = roomService.channelStatus(linkRoomDto.getLink());
		// TODO : 채널 링크가 유효한가? 체크
		if (!channelStatus.isValid()) {
			throw new CustomException(ErrorCode.NOTFOUND_CHANNEL);
		}

		RoomEntryManager roomEntryManager = roomService.isChannelMember(userUuid, linkRoomDto.getLink());

		if (!roomEntryManager.isCheck()) {
			System.out.println("room 에 유저 없음");
			throw new CustomException(ErrorCode.NOTFOUND_ROOM);
		}

		if (userUuid.equals(channelStatus.getOwnerId())){
			Room room = roomService.getRoomByRoomId(linkRoomDto.getRoomId());

			return ResponseEntity.ok(
					RoomEnterDto.builder()
							.roomId(linkRoomDto.getRoomId())
							.channelType(channelStatus.getType())
							.channelTitle(channelStatus.getTitle())
							.filteredLevel(channelStatus.getFilteredLevel())
							.userList(room.getUserList())
							.isClosed(room.isClosed())
							.build()
			);
		}

		Room room = roomService.getRoomByRoomId(linkRoomDto.getRoomId());
		// TODO : room 닫혔을 때 처리? 1 Link, 1 User 분리 필요
		if (room.getUserList().contains(userUuid)){
			System.out.println(1);
			return ResponseEntity.ok(
					RoomEnterDto.builder()
							.roomId(linkRoomDto.getRoomId())
							.channelType(channelStatus.getType())
							.channelTitle(channelStatus.getTitle())
							.filteredLevel(channelStatus.getFilteredLevel())
							.userList(room.getUserList())
							.isClosed(room.isClosed())
							.build()
			);
		}

		System.out.println("room 진입 실패");
		throw new CustomException(ErrorCode.NOTFOUND_ROOM);
	}
}
