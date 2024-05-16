package com.camus.backend.chat.domain.repository;

import java.util.UUID;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.document.NoticeMessage;
import com.camus.backend.chat.domain.dto.FilteredMessageDto;

public interface RedisChatRepository {

	public void createStreamConsumerGroup(String roomId, UUID ownerUserId);

	public void updateStreamConsumerGroup(String roomId, UUID userId, String streamMessageId);

	public String getStreamCount(UUID roomId);

	public void updateStreamCount(UUID roomId);

	public void addNoticeMessage(NoticeMessage noticeMessage);

	public void addCommonMessage(CommonMessage commonMessage);

	public String getLatestRedisMessageId(String roomId);

	public void addFilteredType(FilteredMessageDto filteredMessageDto);

}
