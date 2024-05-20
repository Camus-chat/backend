package com.camus.backend.manage.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelEnterInfoDto {
	private String channelTitle;
	private String channelContent;
	private String ownerNickname;
	private String ownerProfileImage;
	
}
