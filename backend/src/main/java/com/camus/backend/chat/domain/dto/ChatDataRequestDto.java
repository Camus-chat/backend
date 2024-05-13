package com.camus.backend.chat.domain.dto;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatDataRequestDto {
	UUID roomId;
	int page;
}
