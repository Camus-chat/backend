package com.camus.backend.manage.domain.dto;

import java.util.ArrayList;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class RoomDto {
	private UUID roomId;
	//private UUID channelKey;

	private String channelType;
	private String channelTitle;

	private ArrayList<UUID> userList;
	private boolean isClosed;

	private LastMessageInfo lastMessage;
	private int unreadCount;

}
