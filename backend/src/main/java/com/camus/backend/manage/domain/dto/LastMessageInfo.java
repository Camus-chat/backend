package com.camus.backend.manage.domain.dto;

import java.util.UUID;

import com.camus.backend.chat.domain.document.RedisSavedCommonMessage;
import com.camus.backend.chat.domain.document.RedisSavedMessageBasic;
import com.camus.backend.chat.domain.document.RedisSavedNoticeMessage;

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
	private String _class;
	private UUID userId;
	private String content;

	public LastMessageInfo(RedisSavedMessageBasic messageInfo) {
		this._class = messageInfo.get_class();
		this.content = messageInfo.getContent();

		if (messageInfo instanceof RedisSavedNoticeMessage noticeMessage) {
			this.userId = noticeMessage.getTarget();
		} else if (messageInfo instanceof RedisSavedCommonMessage commonMessage) {
			this.userId = commonMessage.getSenderId();
		} else {
			throw new IllegalArgumentException("Invalid message type");
		}
	}

}
