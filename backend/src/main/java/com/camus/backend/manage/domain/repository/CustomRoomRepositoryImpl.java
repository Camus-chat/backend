package com.camus.backend.manage.domain.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.manage.domain.document.Channel;
import com.camus.backend.manage.domain.document.ChannelList;
import com.camus.backend.manage.domain.document.Room;
import com.camus.backend.manage.domain.dto.RoomDto;
import com.camus.backend.manage.util.ChannelStatus;

@Repository
public class CustomRoomRepositoryImpl implements CustomRoomRepository {

	@Autowired
	private MongoTemplate mongoTemplate;

	public List<UUID> getRoomListByLink(UUID link) {

		Query query = new Query(Criteria.where("channels.link").is(link));
		ChannelList channelList = mongoTemplate.findOne(query, ChannelList.class);

		if (channelList != null) {
			return channelList.getChannels().stream()
				.filter(ch -> ch.getLink().equals(link))
				.findFirst()
				.map(Channel::getChatRoomList)
				.orElse(new ArrayList<>());
		} else {
			return new ArrayList<>();
		}
	}

	public List<UUID> getRoomListByOwnerId(UUID ownerId) {
		Query query = new Query(Criteria.where("_id").is(ownerId));
		query.fields().include("channels");
		ChannelList channelList = mongoTemplate.findOne(query, ChannelList.class);
		List<UUID> roomIdList = new ArrayList<>();
		if (channelList == null) {
			return roomIdList;
		}
		for (Channel channel : channelList.getChannels()) {
			roomIdList.addAll(channel.getChatRoomList());
		}
		return roomIdList;
	}

	@Async
	public CompletableFuture<RoomDto> getRoomInfoByRoomId(UUID roomId) {
		// Room 객체를 검색합니다.
		Query query = new Query(Criteria.where("_id").is(roomId));
		Room room = mongoTemplate.findOne(query, Room.class);

		if (room == null) {
			System.out.println("방 못찾음");
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		// 임베디드 문서 내의 key 필드를 기준으로 ChannelList 문서를 검색합니다.
		Query channelQuery = new Query(Criteria.where("channels.key").is(room.getKey()));
		ChannelList channelList = mongoTemplate.findOne(channelQuery, ChannelList.class);

		if (channelList == null || channelList.getChannels() == null) {
			System.out.println("채널 리스트 못찾음");
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		// channels 리스트에서 키가 일치하는 채널을 찾습니다.
		Channel channel = channelList.getChannels().stream()
			.filter(ch -> ch.getKey().equals(room.getKey()))
			.findFirst()
			.orElse(null);

		if (channel == null) {
			System.out.println("채널을 못찾음");
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		// RoomDto 객체를 생성합니다.
		RoomDto roomInfo = new RoomDto();
		roomInfo.setRoomId(room.get_id());
		roomInfo.setChannelType(channel.getType());
		roomInfo.setChannelTitle(channel.getTitle());
		roomInfo.setUserList(room.getUserList());
		roomInfo.setClosed(room.isClosed());

		return CompletableFuture.completedFuture(roomInfo);
	}

	public List<UUID> getUserListById(UUID id) {
		Query query = new Query(Criteria.where("_id").is(id));
		query.fields().include("userList");
		Room room = mongoTemplate.findOne(query, Room.class);
		if (room != null) {
			return room.getUserList();
		} else {
			return new ArrayList<>();
		}
	}

	public ChannelStatus getChannelStatus(UUID channelLink) {
		Query query = new Query(Criteria.where("channels.link").is(channelLink));
		ChannelList channelList = mongoTemplate.findOne(query, ChannelList.class);

		if (channelList == null || channelList.getChannels() == null) {
			return new ChannelStatus(false, null, null, null, null);
		}
		Channel channel = channelList.getChannels().stream()
			.filter(ch -> ch.getLink().equals(channelLink))
			.findFirst()
			.orElse(null);
		if (channel != null) {
			return new ChannelStatus(channel.getIsValid(), channel.getType(), channel.getTitle(),
				channel.getKey(), channelList.get_id());
		} else {
			return new ChannelStatus(false, null, null, null, null);
		}
	}

	public UUID createGroupRoom(UUID channelKey, UUID ownerId) {
		Room newGroupRoom = new Room(channelKey, ownerId);
		mongoTemplate.save(newGroupRoom);
		System.out.println(newGroupRoom.toString());
		return newGroupRoom.get_id();
	}

	public UUID createPrivateRoom(UUID channelKey, UUID ownerId, UUID guestId) {
		Room newPrivateRoom = new Room(channelKey, ownerId);
		newPrivateRoom.getUserList().add(guestId);
		mongoTemplate.save(newPrivateRoom);
		return newPrivateRoom.get_id();
	}

	public UUID getGroupRoomByChannelKey(UUID channelKey, UUID guestId) {
		Query query = new Query(Criteria.where("key").is(channelKey));
		query.fields().include("_id");
		Room room = mongoTemplate.findOne(query, Room.class);
		if (room == null) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
		Update update = new Update().push("userList", guestId);
		mongoTemplate.updateFirst(query, update, Room.class);

		return room.get_id();
	}

	public Boolean isRoomClosed(UUID roomId) {
		Query query = new Query(Criteria.where("_id").is(roomId));
		query.fields().include("isClosed");
		Room room = mongoTemplate.findOne(query, Room.class);
		if (room == null) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
		return room.isClosed();
	}

	public Room getRoomByRoomId(UUID roomId) {
		Query query = new Query(Criteria.where("_id").is(roomId));
		Room room = mongoTemplate.findOne(query, Room.class);
		if (room == null) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
		return room;
	}
}
