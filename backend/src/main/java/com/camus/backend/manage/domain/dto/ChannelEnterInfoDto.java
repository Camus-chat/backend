package com.camus.backend.manage.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelEnterInfoDto {
	private String title;
	private String content;
	//owner member info
	private String nickname;
	private String profileLink;
	private UUID uuid;
	
}
