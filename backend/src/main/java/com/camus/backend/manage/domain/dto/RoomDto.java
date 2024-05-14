package com.camus.backend.manage.domain.dto;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document("Chat_room")
@Getter
@Setter
public class RoomDto {
	private UUID _id;
	private UUID channelKey;

	private ArrayList<UUID> userList;
	private boolean isClosed;

	private int maxUserCount;
}
