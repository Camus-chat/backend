package com.camus.backend.chat.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.chat.service.ChatDataService;
import com.camus.backend.chat.service.RedisChatService;
import com.camus.backend.manage.util.ManageConstants;

@RestController
@RequestMapping("/test")
public class TestController {

	private final RedisChatService redisChatService;
	private final ChatDataService chatDataService;

	private TestController(RedisChatService redisChatService,
		ChatDataService chatDataService) {
		this.redisChatService = redisChatService;
		this.chatDataService = chatDataService;
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

}
