package com.camus.backend.manage.domain.repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.camus.backend.manage.domain.document.Room;
import com.camus.backend.manage.domain.dto.RoomDto;
import com.camus.backend.manage.util.ChannelStatus;

public interface CustomRoomRepository {

	List<UUID> getRoomListByLink(UUID channelLink);

	List<UUID> getUserListById(UUID uuid);

	ChannelStatus getChannelStatus(UUID channelLink);

	UUID createGroupRoom(UUID channelKey, UUID ownerId);

	List<UUID> getRoomListByOwnerId(UUID ownerId);

	CompletableFuture<RoomDto> getRoomInfoByRoomId(UUID roomId);

	UUID createPrivateRoom(UUID channelKey, UUID ownerId, UUID guestId);

	UUID getGroupRoomByChannelKey(UUID channelKey, UUID guestId);

	Boolean isRoomClosed(UUID roomId);

	Room getRoomByRoomId(UUID roomId);

}
