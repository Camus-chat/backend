package com.camus.backend.chat.domain.repository;

import java.util.UUID;

public interface RedisStreamGroupRepository {

	public int getUserUnreadMessageSize(String roomId, UUID userId);
}
