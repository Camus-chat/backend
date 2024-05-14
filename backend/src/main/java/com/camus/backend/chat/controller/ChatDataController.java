package com.camus.backend.chat.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.chat.domain.dto.ChatDataDto;
import com.camus.backend.chat.domain.dto.ChatDataRequestDto;
import com.camus.backend.chat.domain.dto.PaginationDto;
import com.camus.backend.chat.domain.dto.RedisSavedMessageBasicDto;
import com.camus.backend.chat.service.ChatDataService;
import com.camus.backend.chat.util.ChatModules;
import com.camus.backend.manage.util.ManageConstants;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/chat")
public class ChatDataController {

	private final ChatDataService chatDataService;
	private final ChatModules chatModules;

	private ChatDataController(ChatDataService chatDataService
		, ChatModules chatModules) {
		this.chatDataService = chatDataService;
		this.chatModules = chatModules;
	}

	@Operation(
		summary = "읽지 않은 채팅방 채팅 내역",
		description = "읽지 않은 채팅방의 채팅 내역을 반환합니다. "
			+ "\n nextTimeStamp가 \"0-0\"이어야 합니다."
	)
	@PostMapping("/data/unread")
	public ResponseEntity<List<RedisSavedMessageBasicDto>> getUnreadChatData(
		@RequestBody ChatDataRequestDto chatDataRequestDto
		// 사용자 데이터 받아오기
	) {
		UUID tempUUID = ManageConstants.tempMemUuid;

		return ResponseEntity.ok(
			chatDataService.getUserUnreadMessage(
				chatDataRequestDto.getRoomId(),
				tempUUID
			)
		);

	}

	@PostMapping("/data")
	public ResponseEntity<ChatDataDto> getChatData(
		@RequestBody ChatDataRequestDto chatDataRequestDto
	) {

		UUID tempUUID = ManageConstants.tempMemUuid;

		int userUnreadMessageSize = chatDataService.getUserUnreadMessageSize(
			chatDataRequestDto.getRoomId(),
			tempUUID
		);

		if (chatDataRequestDto.getNextMessageTimeStamp().equals("0-0")) {
			// 안 읽은 사이즈가 0이면
			if (userUnreadMessageSize == 0)
				return ResponseEntity.ok(
					new ChatDataDto(
						new ArrayList<RedisSavedMessageBasicDto>(),
						new PaginationDto("0-0", 0)
					)
				);
			// 읽은 사이즈가 1 이상이면

		}

		if (!chatDataRequestDto.getNextMessageTimeStamp().equals("0-0")
		) {

			// 여기는 내내 Redis
			return ResponseEntity.badRequest().build();
		}

		// TODO  : Mongo에서 가져옴

		return ResponseEntity.badRequest().build();
	}

	;
}
