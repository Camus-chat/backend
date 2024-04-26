package com.camus.backend.manage.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelDto extends CreateChannelDto {

	private String link;

	public ChannelDto() {
		super();
	}

	public ChannelDto(CreateChannelDto createChannelDto, String link) {
		this.setType(createChannelDto.getType());
		this.setTitle(createChannelDto.getTitle());
		this.setContent(createChannelDto.getContent());
		this.setFilterLevel(createChannelDto.getFilterLevel());
		this.link = link;
	}

}
