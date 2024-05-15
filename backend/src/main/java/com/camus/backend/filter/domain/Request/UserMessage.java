package com.camus.backend.filter.domain.Request;

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

	public UserMessage(CommonMessage message){
		id = message.getMessageId();
		content = message.getContent();
		userId = message.getSenderId();
	}
}
