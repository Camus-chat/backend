package com.camus.backend.chat.domain.dto.chatmessagedto;

import java.util.UUID;

import com.camus.backend.chat.domain.document.RedisSavedNoticeMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeMessageDto extends MessageBasicDto {
	private UUID targetId;
	private String noticeType;

	public NoticeMessageDto(RedisSavedNoticeMessage redisSavedNoticeMessage) {
		super(redisSavedNoticeMessage);
		this.targetId = redisSavedNoticeMessage.getTarget();
		this.noticeType = redisSavedNoticeMessage.getNoticeType();
	}

}
