package com.camus.backend.chat.controller;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestChatRequest {
	private UUID roomId;
	private String content;
}
