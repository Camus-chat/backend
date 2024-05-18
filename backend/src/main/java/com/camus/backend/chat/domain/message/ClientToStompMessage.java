package com.camus.backend.chat.domain.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientToStompMessage {
	@JsonProperty("roomId")
	private String roomId;
	@JsonProperty("content")
	private String content;
}
