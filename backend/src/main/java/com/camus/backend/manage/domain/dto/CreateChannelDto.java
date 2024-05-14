package com.camus.backend.manage.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateChannelDto {
	private String type;
	private String title;
	private String content;
	private Integer filterLevel;

	public CreateChannelDto() {

	}

}
