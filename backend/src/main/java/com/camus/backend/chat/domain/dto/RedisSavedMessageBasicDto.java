package com.camus.backend.chat.domain.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.document.NoticeMessage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisSavedMessageBasicDto {
	private String messageType; // Notice, Common
	private Long messageId;
	private UUID roomId;
	private LocalDateTime createdDate;
	private String content;

	public RedisSavedMessageBasicDto() {
	}

	public RedisSavedMessageBasicDto(CommonMessage commonMessage) {
		//FIXME : messageType을 enum으로 변경
		this.messageType = "Common";
		this.messageId = commonMessage.getMessageId();
		this.roomId = commonMessage.getRoomId();
		this.createdDate = commonMessage.getCreatedDate();
		this.content = commonMessage.getContent();
	}

	public RedisSavedMessageBasicDto(NoticeMessage noticeMessage) {
		//FIXME : messageType을 enum으로 변경
		this.messageType = "Notice";
		this.messageId = noticeMessage.getMessageId();
		this.roomId = noticeMessage.getRoomId();
		this.createdDate = noticeMessage.getCreatedDate();
		this.content = noticeMessage.getContent();
	}

	@Override
	public String toString() {
		return "RedisSavedMessageBasicDto{" +
			"messageType='" + messageType + '\'' +
			", messageId=" + messageId +
			", roomId=" + roomId +
			", createdDate=" + createdDate +
			", content='" + content + '\'' +
			'}';
	}
}
