package com.camus.backend.manage.domain.document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document
@TypeAlias("CHAT_ROOM")
public class Room {

	private UUID _id;
	private UUID key;
	private LocalDateTime createDate;
	private ArrayList<UUID> userList;
	private boolean isClosed = false;
	private int maxUserCount;

	Room(UUID channelKey) {
		this._id = UUID.randomUUID();
		this.key = UUID.randomUUID();
		this.createDate = LocalDateTime.now();
		this.userList = new ArrayList<>();
	}

}
