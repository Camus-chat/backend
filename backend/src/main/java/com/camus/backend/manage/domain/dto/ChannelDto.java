package com.camus.backend.manage.domain.dto;

import java.util.UUID;

import com.camus.backend.manage.domain.document.Channel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelDto extends CreateChannelDto {

	private String link;
	// FIXME : link 앞선 String 수정;
	private String linkBase = "http://localhost:8080/channel/";

	public ChannelDto() {
		super();
	}

	public ChannelDto(CreateChannelDto createChannelDto, UUID link) {
		this.setType(createChannelDto.getType());
		this.setTitle(createChannelDto.getTitle());
		this.setContent(createChannelDto.getContent());
		this.setFilterLevel(createChannelDto.getFilterLevel());
		this.link = linkBase + link;
	}

	public ChannelDto(Channel channel) {
		this.link = linkBase + channel.getLink();
		this.setType(channel.getType());
		this.setTitle(channel.getTitle());
		this.setContent(channel.getContent());
		this.setFilterLevel(channel.getFilterLevel());
	}

}
