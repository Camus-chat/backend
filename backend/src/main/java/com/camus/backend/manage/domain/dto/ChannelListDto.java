package com.camus.backend.manage.domain.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChannelListDto {
	private List<ChannelDto> channelList;
}
