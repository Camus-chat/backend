package com.camus.backend.manage.domain.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChannelInfoDto {
	private UUID link;
	private String title;
	private String content;
	private int filterLevel;
}
