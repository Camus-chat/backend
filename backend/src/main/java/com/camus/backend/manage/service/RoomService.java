package com.camus.backend.manage.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.RedisSavedMessageBasic;
import com.camus.backend.chat.service.ChatDataService;
import com.camus.backend.chat.service.RedisChatService;
import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.manage.domain.dto.LastMessageInfo;
import com.camus.backend.manage.domain.dto.RoomDto;
import com.camus.backend.manage.domain.repository.ChannelListRepository;
import com.camus.backend.manage.domain.repository.RoomRepository;
import com.camus.backend.manage.util.ChannelStatus;
import com.camus.backend.manage.util.RoomEntryManager;

@Service
public class RoomService {

	private final RoomRepository roomRepository;
	private final RedisChatService redisChatService;
	private final ChatDataService chatDataService;
	private final ChannelListRepository channelListRepository;

	RoomService(RoomRepository roomRepository, RedisChatService redisChatService,
		ChannelListRepository channelListRepository,
		ChatDataService chatDataService) {
		this.roomRepository = roomRepository;
		this.redisChatService = redisChatService;
		this.channelListRepository = channelListRepository;
		this.chatDataService = chatDataService;
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

	public List<RoomDto> getRoomListByOwnerId(UUID ownerId) {
		List<UUID> roomIdList = roomRepository.getRoomListByOwnerId(ownerId);
		List<CompletableFuture<RoomDto>> futures = new ArrayList<>();

		for (UUID roomId : roomIdList) {
			RedisSavedMessageBasic latestMessage = chatDataService.getLatestRedisMessageId(roomId.toString());
			int unreadCount = chatDataService.UnreadMessageCountByUserId(roomId, ownerId);
			futures.add(CompletableFuture.supplyAsync(() -> {
				try {
					RoomDto roomDto = roomRepository.getRoomInfoByRoomId(roomId).join();
					// 최신 메시지를 가져와서 설정
					roomDto.setLastMessage(new LastMessageInfo(latestMessage));
					// 읽지 않은 메시지 개수를 가져와서 설정
					roomDto.setUnreadCount(unreadCount);
					return roomDto;
				} catch (Exception e) {
					// 예외 처리 및 로깅
					e.printStackTrace();
					return null; // 예외 발생 시 null 반환
				}
			}));
		}

		return futures.stream()
			.map(CompletableFuture::join)
			.collect(Collectors.toList());
	}

	// FeatureID 511-2 : 새로운 Rooom 생성 및 channel 정보에 추가

	// GroupRoom의 경우
	public UUID createGroupRoomByOwnerId(UUID channelKey, UUID ownerId) {
		// 그룹 채널 생성 시 바로 방 생성
		UUID newRoomId = roomRepository.createGroupRoom(channelKey, ownerId);
		System.out.println("newRoomId :" + newRoomId.toString());

		// 레디스에 해당 방 생성 notice 올리기
		redisChatService.createChatRoomNotice(newRoomId.toString(), ownerId);

		return newRoomId;
	}

	public UUID createPrivateRoomByGuestId(UUID channelKey, UUID ownerId, UUID guestId) {

		// 개인 채널 생성 시 바로 방 생성
		UUID newRoomId = roomRepository.createPrivateRoom(channelKey, ownerId, guestId);
		//채널 정보에 방추가
		channelListRepository.addRoomIdToChannel(newRoomId, channelKey);

		// 레디스에 해당 방 생성 notice 올리기
		redisChatService.createChatRoomNotice(newRoomId.toString(), ownerId);

		// TODO  : guest유저 진입 메시지 보내기
		redisChatService.newUserEnterRoomNotice(newRoomId.toString(), guestId);

		return newRoomId;
	}

	public UUID joinGroupRoom(UUID channelKey, UUID guestId) {
		// 사용자 진입
		UUID roomId = roomRepository.getGroupRoomByChannelKey(channelKey, guestId);

		// TODO  : guest유저 진입 메시지 보내기
		redisChatService.newUserEnterRoomNotice(roomId.toString(), guestId);
		return roomId;
	}

	public ChannelStatus channelStatus(UUID channelLink) {
		ChannelStatus channelStatus = roomRepository.getChannelStatus(channelLink);
		if (channelStatus.getKey() == null) {
			throw new CustomException(ErrorCode.NOTFOUND_CHANNEL);
		}
		return channelStatus;
	}

}

