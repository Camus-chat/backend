package com.camus.backend.manage.domain.document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "CHAT_ROOM")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Room {

	private UUID _id;
	private UUID key;
	private LocalDateTime createDate;
	private ArrayList<UUID> userList;
	private boolean isClosed = false;
	private int maxUserCount;

	public Room(UUID channelKey, UUID ownerId) {
		this._id = UUID.randomUUID();
		this.key = channelKey;
		this.createDate = LocalDateTime.now();
		this.userList = new ArrayList<>();
		this.userList.add(ownerId);

	}

}
