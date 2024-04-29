package com.camus.backend.manage.domain.repository;

import java.util.UUID;

import com.camus.backend.manage.domain.document.Channel;
import com.camus.backend.manage.domain.document.ChannelList;

public interface CustomChannelListRepository {
	void addChannelToMemberChannels(UUID userUuid, Channel channel);

	ChannelList getChannelListByMemberId(UUID userId);

	void disableChannelByLink(String channellink, UUID userUuid);
}
