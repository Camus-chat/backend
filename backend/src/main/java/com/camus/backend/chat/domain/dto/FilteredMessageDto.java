package com.camus.backend.chat.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FilteredMessageDto {
	private UUID roomId;
	private long messageId;
	private String filteredLevel;
	private Short filteredType;
	private LocalDateTime createdDate;
}
