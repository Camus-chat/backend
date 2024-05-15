package com.camus.backend.manage.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import org.springframework.stereotype.Repository;


import com.camus.backend.manage.domain.document.Channel;
import com.camus.backend.manage.domain.document.Room;
import com.camus.backend.manage.domain.dto.RoomDto;
import com.camus.backend.manage.util.ChannelStatus;

@Repository
public class CustomRoomRepositoryImpl implements CustomRoomRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	public List<UUID> getRoomListByLink(UUID link) {
		Query query = new Query(Criteria.where("link").is(link));
		query.fields().include("chat_room_list");
		Channel channel = mongoTemplate.findOne(query, Channel.class);
		if (channel != null) {
			return channel.getChatRoomList();
		} else {
			return new ArrayList<>();
		}
	}

	public List<UUID> getUserListById(UUID id) {
		Query query = new Query(Criteria.where("_id").is(id));
		query.fields().include("userList");
		RoomDto roomDto = mongoTemplate.findOne(query, RoomDto.class);
		if (roomDto != null) {
			return roomDto.getUserList();
		} else {
			return new ArrayList<>();
		}
	}

	public ChannelStatus getChannelStatus(UUID channelLink) {
		Query query = new Query(Criteria.where("link").is(channelLink));
		Channel channel = mongoTemplate.findOne(query, Channel.class);
		if (channel != null) {
			return new ChannelStatus(channel.getIsValid(), channel.getType());
		} else {
			return new ChannelStatus(false, null);
		}
	}

	public UUID createGroupRoom(UUID channelKey, UUID ownerId) {
		Room newGroupRoom = new Room(channelKey, ownerId);
		mongoTemplate.save(newGroupRoom);
		return newGroupRoom.get_id();
	}

}
