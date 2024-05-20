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
public class RoomEnterDto {

	UUID roomId;

	String channelType;

	String channelTitle;
	int filteredLevel;

	List<UUID> memberList;
	List<UUID> userList;

	Boolean isClosed;

}
