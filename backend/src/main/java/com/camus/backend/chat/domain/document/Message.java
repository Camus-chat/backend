package com.camus.backend.chat.domain.document;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Document(collection = "CHAT_MESSAGE")
@FieldNameConstants
@Getter
@Setter
public class Message {
	private Long messageId;
	private UUID roomId;
	private LocalDateTime createdDate;
	private String content;
}
