package com.camus.backend.manage.domain.dto;

import java.util.UUID;

import com.camus.backend.chat.domain.dto.chatmessagedto.CommonMessageDto;
import com.camus.backend.chat.domain.dto.chatmessagedto.MessageBasicDto;
import com.camus.backend.chat.domain.dto.chatmessagedto.NoticeMessageDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LastMessageInfo {
	private String type;
	private UUID userId;
	private String content;
	private String filterLevel;

	public LastMessageInfo(MessageBasicDto messageInfo) {
		this.type = messageInfo.getType();
		this.content = messageInfo.getContent();

		if (messageInfo instanceof NoticeMessageDto noticeMessage) {
			this.userId = noticeMessage.getTargetId();
		} else if (messageInfo instanceof CommonMessageDto commonMessage) {
			this.userId = commonMessage.getSenderId();
			this.filterLevel = commonMessage.getFilteredLevel();
		} else {
			throw new IllegalArgumentException("Invalid message type");
		}
	}

}
