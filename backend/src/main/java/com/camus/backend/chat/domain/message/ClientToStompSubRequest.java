package com.camus.backend.chat.domain.message;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientToStompSubRequest {
	@JsonProperty("roomId")
	private UUID roomId;
	@JsonProperty("userToken")
	private String userToken;
}
