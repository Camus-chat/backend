package com.camus.backend.manage.domain.dto;

import java.util.UUID;

import com.camus.backend.manage.domain.document.Channel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelDto extends CreateChannelDto {

	private UUID link;

	public ChannelDto() {
		super();
	}

	public ChannelDto(CreateChannelDto createChannelDto, UUID link) {
		this.setType(createChannelDto.getType());
		this.setTitle(createChannelDto.getTitle());
		this.setContent(createChannelDto.getContent());
		this.setFilterLevel(createChannelDto.getFilterLevel());
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
