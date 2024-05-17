package com.camus.backend.filter.domain.Request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.document.Message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserMessage {
	private Long id;
	private String content;
	private UUID userId;
	LocalDateTime createdDate;
	public UserMessage(CommonMessage message){
		id = message.getMessageId();
		content = message.getContent();
		userId = message.getSenderId();
		createdDate = message.getCreatedDate();
	}
}
