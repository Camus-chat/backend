package com.camus.backend.chat.domain.dto;

import java.util.UUID;

import com.camus.backend.chat.domain.document.NoticeMessage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class RedisSavedNoticeMessageDto extends RedisSavedMessageBasicDto {
	private UUID target;
	private String noticeType;

	public RedisSavedNoticeMessageDto() {
		super();
	}

	public RedisSavedNoticeMessageDto(NoticeMessage noticeMessage) {
		super(noticeMessage);
		this.target = noticeMessage.getTarget();
		this.noticeType = noticeMessage.getNoticeType();
	}

	public String toString() {
		return super.toString() + "target : " + target + "\n" + "noticeType : " + noticeType + "\n";
	}

}
