package com.camus.backend.chat.domain.repository;

import java.util.UUID;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.document.NoticeMessage;

public interface RedisChatRepository {
	public String getStreamCount(UUID roomId);

	public void addNoticeMessage(NoticeMessage noticeMessage);

	public void addCommonMessage(CommonMessage commonMessage);

	public String getLatestRedisMessageId(UUID roomId);

	public void setLastMessageId(UUID roomId, String messageId);

}
