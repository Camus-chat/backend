package com.camus.backend.chat.domain.dto.chatmessagedto;

import java.util.UUID;

import com.camus.backend.chat.domain.document.RedisSavedCommonMessage;
import com.camus.backend.chat.domain.document.RedisSavedNoticeMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageBasicDto {
	private String type;
	private Long messageId;
	private UUID roomId;
	private String createdDate;
	private String content;

	MessageBasicDto(RedisSavedCommonMessage redisSavedCommonMessage) {
		this.type = redisSavedCommonMessage.get_class();
		this.messageId = redisSavedCommonMessage.getMessageId();
		this.roomId = redisSavedCommonMessage.getRoomId();
		this.createdDate = redisSavedCommonMessage.getCreatedDate().toLocalDate().toString();
		this.content = redisSavedCommonMessage.getContent();
	}

	MessageBasicDto(RedisSavedNoticeMessage redisSavedNoticeMessage) {
		this.type = redisSavedNoticeMessage.get_class();
		this.messageId = redisSavedNoticeMessage.getMessageId();
		this.roomId = redisSavedNoticeMessage.getRoomId();
		this.createdDate = redisSavedNoticeMessage.getCreatedDate().toLocalDate().toString();
		this.content = redisSavedNoticeMessage.getContent();
	}
}
