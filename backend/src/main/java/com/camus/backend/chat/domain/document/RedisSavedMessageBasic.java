package com.camus.backend.chat.domain.document;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisSavedMessageBasic {
	private String _class; // Notice, Common
	private Long messageId;
	private UUID roomId;
	private LocalDateTime createdDate;
	private String content;

	public RedisSavedMessageBasic() {
	}

	public RedisSavedMessageBasic(CommonMessage commonMessage) {
		//FIXME : messageType을 enum으로 변경
		this._class = "Common";
		this.messageId = commonMessage.getMessageId();
		this.roomId = commonMessage.getRoomId();
		this.createdDate = commonMessage.getCreatedDate();
		this.content = commonMessage.getContent();
	}

	public RedisSavedMessageBasic(NoticeMessage noticeMessage) {
		//FIXME : messageType을 enum으로 변경
		this._class = "Notice";
		this.messageId = noticeMessage.getMessageId();
		this.roomId = noticeMessage.getRoomId();
		this.createdDate = noticeMessage.getCreatedDate();
		this.content = noticeMessage.getContent();
	}

	@Override
	public String toString() {
		return "RedisSavedMessageBasicDto{" +
			"messageType='" + _class + '\'' +
			", messageId=" + messageId +
			", roomId=" + roomId +
			", createdDate=" + createdDate +
			", content='" + content + '\'' +
			'}';
	}
}
