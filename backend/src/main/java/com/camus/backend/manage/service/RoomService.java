package com.camus.backend.manage.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.camus.backend.chat.service.RedisChatService;
import com.camus.backend.manage.domain.repository.RoomRepository;
import com.camus.backend.manage.util.ChannelStatus;
import com.camus.backend.manage.util.RoomEntryManager;

@Service
public class RoomService {

	private final RoomRepository roomRepository;
	private final RedisChatService redisChatService;

	RoomService(RoomRepository roomRepository, RedisChatService redisChatService) {
		this.roomRepository = roomRepository;
		this.redisChatService = redisChatService;
	}

	// FeatureID 511-1 : 기존 채널 참여 여부 확인
	public RoomEntryManager isChannelMember(UUID memberId, UUID channelLink) {

		List<UUID> roomListByLink = roomRepository.getRoomListByLink(channelLink);
		for (int i = 0; i < roomListByLink.size(); i++) {
			List<UUID> userListById = roomRepository.getUserListById(roomListByLink.get(i));
			if (userListById.contains(memberId)) {
				return new RoomEntryManager(true, roomListByLink.get(i));
			}
		}

		return new RoomEntryManager(false, null);
	}

	// FeatureID 511-2 : 새로운 Rooom 생성 및 channel 정보에 추가

	// GroupRoom의 경우
	public UUID createGroupRoomByOwnerId(UUID channelKey, UUID ownerId) {
		// 그룹 채널 생성 시 바로 방 생성
		UUID newRoomId = roomRepository.createGroupRoom(channelKey, ownerId);

		// 레디스에 해당 방 생성 notice 올리기
		redisChatService.createChatRoomNotice(newRoomId.toString(), ownerId);
		System.out.println("newRoomId :" + newRoomId.toString());
		return newRoomId;
	}

	public ChannelStatus channelStatus(UUID channelLink) {

		return roomRepository.getChannelStatus(channelLink);
	}

}

