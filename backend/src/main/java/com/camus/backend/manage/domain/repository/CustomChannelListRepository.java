package com.camus.backend.manage.domain.repository;

import java.util.UUID;

import com.camus.backend.manage.domain.document.Channel;
import com.camus.backend.manage.domain.document.ChannelList;
import com.camus.backend.manage.domain.dto.ChannelInfoDto;

public interface CustomChannelListRepository {
	void addChannelToMemberChannels(UUID memberId, Channel channel);

	ChannelList getChannelListByMemberId(UUID memberId);

	void disableChannelByLink(UUID link, UUID memberId);

	void editChannelInfo(UUID memeberId, ChannelInfoDto channelInfoDto);

	void addRoomIdToChannel(UUID roomId, UUID channelKey);
	UUID getChannelKeyByChannelLink(UUID channelLink);
}
