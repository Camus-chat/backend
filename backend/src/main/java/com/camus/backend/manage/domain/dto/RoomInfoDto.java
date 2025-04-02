package com.camus.backend.manage.domain.dto;

import java.util.List;
import java.util.UUID;

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
public class RoomInfoDto {

	private UUID roomId;

	private String channelType;

	private String channelTitle;
	private int filteredLevel;

	private List<UUID> userList;

	private Boolean isClosed;

}
