package com.camus.backend.manage.domain.dto;

import java.util.UUID;

import com.camus.backend.manage.domain.document.Channel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelDto extends ChannelCreateDto {

	private UUID link;

	public ChannelDto() {
		super();
	}

	public ChannelDto(ChannelCreateDto channelCreateDto, UUID link) {
		this.setType(channelCreateDto.getType());
		this.setTitle(channelCreateDto.getTitle());
		this.setContent(channelCreateDto.getContent());
		this.setFilterLevel(channelCreateDto.getFilterLevel());
		this.link = link;
	}

	public ChannelDto(Channel channel) {
		this.link = channel.getLink();
		this.setType(channel.getType());
		this.setTitle(channel.getTitle());
		this.setContent(channel.getContent());
		this.setFilterLevel(channel.getFilterLevel());
	}

}
