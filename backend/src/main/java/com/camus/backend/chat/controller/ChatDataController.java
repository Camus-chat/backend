package com.camus.backend.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.chat.domain.dto.ChatDataRequestDto;
import com.camus.backend.chat.domain.dto.RedisSavedMessageBasicDto;
import com.camus.backend.chat.service.ChatDataService;
import com.camus.backend.chat.util.ChatModules;
import com.camus.backend.manage.util.ManageConstants;

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

	@PostMapping("/data")
	public ResponseEntity<List<RedisSavedMessageBasicDto>> getChatData(
		@RequestBody ChatDataRequestDto chatDataRequestDto
		// 사용자 데이터 받아오기
	) {
		long lastMessageId = chatDataService.getLastMessageIdOfRedis(chatDataRequestDto.getRoomId());
		long streamMessageCount = chatDataService.getStreamCountOfRedis(chatDataRequestDto.getRoomId());

		String tempUUID = ManageConstants.tempMemUuid.toString();

		// TODO : 사용자가 안 읽은 메시지 사이즈 체크 > 300까지만 되도록 만들기

		if (chatDataRequestDto.getPage() == 0) {

			// 0 페이지기 때문에 Redis에서 내가 읽지 않은 시점부터의 메시지를 가져온다
			// 300까지 - lastMesageId가 작으면 그 밑까지만.

		}

		if (chatDataRequestDto.getPage() <=
			chatModules.getMogoDBStartPageIndex(lastMessageId, streamMessageCount)
		) {

			// 여기는 내내 Redis
			return ResponseEntity.badRequest().build();
		}

		// TODO  : Mongo에서 가져옴

		return ResponseEntity.badRequest().build();
	}

	;
}
