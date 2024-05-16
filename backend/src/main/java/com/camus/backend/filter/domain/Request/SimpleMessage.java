package com.camus.backend.filter.domain.Request;

import com.camus.backend.chat.domain.document.CommonMessage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimpleMessage {
	private Long id;
	private String content;

	public SimpleMessage(CommonMessage message){
		id = message.getMessageId();
		content = message.getContent();
	}
}
