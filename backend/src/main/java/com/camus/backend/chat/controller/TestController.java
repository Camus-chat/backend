package com.camus.backend.chat.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.repository.RedisChatRepository;
import com.camus.backend.chat.service.ChatDataService;
import com.camus.backend.chat.service.RedisChatService;
import com.camus.backend.manage.util.ManageConstants;

@RestController
@RequestMapping("/test")
public class TestController {

	private final RedisChatService redisChatService;
	private final ChatDataService chatDataService;
	private final RedisChatRepository redisChatRepository;

	private final SocketController socketController;

	private TestController(RedisChatService redisChatService,
		ChatDataService chatDataService,
		RedisChatRepository redisChatRepository,
		SocketController socketController) {
		this.redisChatService = redisChatService;
		this.chatDataService = chatDataService;
		this.redisChatRepository = redisChatRepository;
		this.socketController = socketController;
	}

	@PostMapping("/redisCreateRoomNoticeTest")
	public ResponseEntity<String> redisTest(
		@RequestBody UUID roomId
	) {

		UUID tempMemberId = ManageConstants.tempMemUuid;
		redisChatService.createChatRoomNotice(
			roomId.toString(),
			tempMemberId
		);

		return ResponseEntity.ok("ok");
	}

	@PostMapping("/redisSendMessagesTest")
	public ResponseEntity<String> redisSendMessagesTest(
		@RequestBody UUID roomId
	) {
		UUID tempMemberId = ManageConstants.tempMemUuid;
		redisChatRepository.addCommonMessage(
			CommonMessage.builder()
				.roomId(roomId)
				.senderId(tempMemberId)
				.content("test입니다. 테스트라구요")
				.filteredType("1")
				._class("CommonMessage")
				.createdDate(LocalDateTime.now())
				.build()
		);

		return ResponseEntity.ok("ok");
	}

}
