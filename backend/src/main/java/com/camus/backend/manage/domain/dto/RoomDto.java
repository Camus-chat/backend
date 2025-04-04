package com.camus.backend.manage.domain.dto;

import java.util.ArrayList;
import java.util.UUID;

import com.camus.backend.chat.domain.dto.chatmessagedto.MessageBasicDto;
import com.fasterxml.jackson.annotation.JsonProperty;
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
	@JsonProperty("isClosed")
	private boolean isClosed;

	private int filteredLevel;

	private MessageBasicDto lastMessage;
	private int unreadCount;

}
