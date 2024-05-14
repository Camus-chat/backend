package com.camus.backend.chat.domain.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatDataRequestDto {
	UUID roomId;
	String nextMessageTimeStamp = "0-0";
}