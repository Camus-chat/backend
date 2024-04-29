package com.camus.backend.manage.domain.dto;

import java.util.List;

import com.camus.backend.manage.domain.document.Channel;
import com.camus.backend.manage.domain.document.ChannelList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelListDto {
	private List<ChannelDto> channelList;

	public ChannelListDto() {
	}

	public ChannelListDto(ChannelList channelList) {

		// ㅊ
		for (int i = 0; i < channelList.getChannels().size(); i++) {
			Channel channel = channelList.getChannels().get(i);
			// 유효 채널 검사
			if (!channel.getIsValid())
				continue;
			this.channelList.add(
				new ChannelDto(channel)
			);
		}
	}

}
