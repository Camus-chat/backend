package com.camus.backend.chat.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FilteredMessageDto {
	@JsonProperty("roomId")
	private UUID roomId;
	@JsonProperty("messageId")
	private long messageId;
	@JsonProperty("filteredLevel")
	private int filteredLevel;
	@JsonProperty("filteredType")
	private Short filteredType;
	@JsonProperty("createdDate")
	private LocalDateTime createdDate;
}
