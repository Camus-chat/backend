package com.camus.backend.chat.domain.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StompToRedisMessage {
	@JsonProperty("roomId")
	private String roomId;
	@JsonProperty("content")
	private String content;
	@JsonProperty("userId")
	private String userId;
}
