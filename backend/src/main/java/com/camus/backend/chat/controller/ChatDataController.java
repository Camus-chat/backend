package com.camus.backend.chat.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.chat.domain.dto.ChatDataListDto;
import com.camus.backend.chat.domain.dto.ChatDataRequestDto;
import com.camus.backend.chat.domain.dto.chatmessagedto.MessageBasicDto;
import com.camus.backend.chat.domain.message.RoomExitResponse;
import com.camus.backend.chat.domain.message.RoomIdRequest;
import com.camus.backend.chat.service.ChatDataService;
import com.camus.backend.member.domain.dto.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/chat")
public class ChatDataController {

	private final ChatDataService chatDataService;
	private ChatDataController(ChatDataService chatDataService) {
		this.chatDataService = chatDataService;
	}

	@Operation(
		summary = "읽지 않은 채팅방 채팅 내역",
		description = "읽지 않은 채팅방의 채팅 내역을 반환합니다. "
			+ "\n nextTimeStamp가 \"0-0\"이어야 합니다."
	)
	@PostMapping("/data/unread")
	public ResponseEntity<List<MessageBasicDto>> getUnreadChatData(
		@RequestBody RoomIdRequest roomIdRequest
		// 사용자 데이터 받아오기
	) {
		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		return ResponseEntity.ok(
			chatDataService.getUserUnreadMessage(
				roomIdRequest.getRoomId(),
				userUuid
			)
		);
	}

	@Operation(
		summary = "채팅방을 나갈 때를 트리거 하는 api입니다.",
		description = "여태까지 읽은 가장 최신 메시지 기록을 재작성합니다."
	)
	@PostMapping("/room/exit")
	public ResponseEntity<RoomExitResponse> exitRoom(
		@RequestBody RoomIdRequest roomIdRequest
	) {

		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		chatDataService.exitRoomUpdateAlreadyRead(
			roomIdRequest.getRoomId(),
			userUuid
		);

		return ResponseEntity.ok(
			RoomExitResponse.builder()
				.exitSuccess(true)
				.build()
		);
	}

	@Operation(
		summary = "채팅방 채팅 내역 무한스크롤",
		description = "채팅방의 채팅 내역을 반환합니다. "
			+ "\n nextTimeStamp를 통해 다음 페이지를 요청합니다."
	)
	@PostMapping("/data")
	public ResponseEntity<ChatDataListDto> getChatData(
		@RequestBody ChatDataRequestDto chatDataRequestDto
	) {

		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		return ResponseEntity.ok(
			chatDataService.getMessagesByPagination(
				chatDataRequestDto.getRoomId(),
				chatDataRequestDto.getNextMessageTimeStamp(),
				userUuid
			)
		);
	}

}
