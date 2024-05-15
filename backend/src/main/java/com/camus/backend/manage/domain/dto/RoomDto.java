package com.camus.backend.manage.domain.dto;

import java.util.ArrayList;
import java.util.UUID;

import com.camus.backend.chat.domain.dto.RedisSavedMessageBasicDto;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class RoomDto {
	private UUID _id;
	//private UUID channelKey;

	private String channelType;
	private String channelTitle;

	private ArrayList<UUID> userList;
	private boolean isClosed;

	private LastMessageInfo lastMessage;
	private int unreadCount;


}
