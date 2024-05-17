package com.camus.backend.chat.domain.document;

import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document
public class RedisSavedNoticeMessage extends RedisSavedMessageBasic {
	private UUID target;
	private String noticeType;

	public RedisSavedNoticeMessage() {
		super();
	}

	public RedisSavedNoticeMessage(NoticeMessage noticeMessage) {
		super(noticeMessage);
		this.target = noticeMessage.getTarget();
		this.noticeType = noticeMessage.getNoticeType();
	}

	public String toString() {
		return super.toString() + "target : " + target + "\n" + "noticeType : " + noticeType + "\n";
	}

}
