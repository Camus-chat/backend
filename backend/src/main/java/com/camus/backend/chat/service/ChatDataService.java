package com.camus.backend.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.camus.backend.chat.domain.dto.ChatDataListDto;
import com.camus.backend.chat.domain.dto.PaginationDto;
import com.camus.backend.chat.util.ChatModules;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.Message;
import com.camus.backend.chat.domain.dto.RedisSavedMessageBasicDto;
import com.camus.backend.chat.domain.repository.MongoChatRepository;
import com.camus.backend.chat.domain.repository.RedisChatRepository;
import com.camus.backend.chat.domain.repository.RedisStreamGroupRepository;

@Service
public class ChatDataService {
	private final RedisChatRepository redisChatRepository;
	private final MongoChatRepository mongoChatRepository;
	private final ChatModules chatModules;

	private final RedisStreamGroupRepository redisStreamGroupRepository;

	ChatDataService(RedisChatRepository redisChatRepository, MongoChatRepository mongoChatRepository,
		RedisStreamGroupRepository redisStreamGroupRepository,
					ChatModules chatModules) {
		this.redisChatRepository = redisChatRepository;
		this.mongoChatRepository = mongoChatRepository;
		this.redisStreamGroupRepository = redisStreamGroupRepository;
		this.chatModules = chatModules;
	}

	public long getStreamCountOfRedis(
		UUID roomId
	) {
		return Long.parseLong(redisChatRepository.getStreamCount(
			roomId
		));
	}

	// 사용자의 가장 최근에 읽은 메시지를 가장 최근으로 당깁니다.
	public void exitRoomUpdateAlreadyRead(
		UUID roomId,
		UUID userId
	) {
		String latestRedisMessageId = redisStreamGroupRepository.getLatestRedisMessageIdFromStream(
			roomId.toString()
		);
		// 사용자가 읽은 메시지의 위치를 최신 메시지로 갱신
		redisStreamGroupRepository.updateStreamConsumerAlreadyReadRedisMessageId(
			roomId.toString(),
			userId, latestRedisMessageId
		);
	}


	// 사용자가 아직 읽지 않은 메시지들을 반환한다
	public ChatDataListDto getUserUnreadMessage(
		UUID roomId,
		UUID userId
	) {

		// 읽어와야 하는 시작점 위치
		String startReadRedisMessageId = redisStreamGroupRepository.getStreamConsumerAlreadyReadRedisMessageId(
			roomId.toString(),
			userId
		);
		List<RedisSavedMessageBasicDto> messages = new ArrayList<>();


		String latestRedisMessageId = redisStreamGroupRepository.getLatestRedisMessageIdFromStream(roomId.toString());

		System.out.println("startReadRedisMessageId : " + startReadRedisMessageId);
		System.out.println("latestRedisMessageId : " + latestRedisMessageId);
		// 이미 최신 메시지까지 읽었다면 빈배열 반환
		if (latestRedisMessageId
			.equals(startReadRedisMessageId)) {
			return 	new ChatDataListDto(
					messages,
					new PaginationDto(startReadRedisMessageId, messages.size())
			);
		}

		// 메시지를 불러오기
		messages = redisStreamGroupRepository.getMessagesFromRedisByEndId(
			roomId.toString(),
			startReadRedisMessageId
		);
		// 마지막으로 읽은 메시지는 바로 최신 메시지
		PaginationDto paginationDto = new PaginationDto(
			latestRedisMessageId,
				messages.size()
		);
		// Redis에 가장 최근에 읽은 값 갱신
		redisStreamGroupRepository.updateStreamConsumerAlreadyReadRedisMessageId(
			roomId.toString(),
			userId,
			latestRedisMessageId
		);

		return new ChatDataListDto(
			messages,
			paginationDto
		);
	}

	public ChatDataListDto getMessagesByPagination(
		UUID roomId,
		String startRedisMessageId
	) {
		if(!startRedisMessageId.contains("DB")){
			return redisStreamGroupRepository.getMessagesFromRedisByStartId(
					roomId.toString(),
					startRedisMessageId
			);
		}

		// 키에 DB가 있을 경우
		String[] split = startRedisMessageId.split("DB");

		long startMessageId = Long.parseLong(split[1]);
		List<Message> messages = mongoChatRepository.getMessagesFromMongoByPage(
			roomId,
			startMessageId
		);

		String nextMessageId = chatModules.getMongoDbMessageKey(messages.get(0).getMessageId().toString());

		// TODO : 메시지를 client가 받을 형태로 파싱하기
		return new ChatDataListDto(
			new ArrayList<>(),
			new PaginationDto(nextMessageId, messages.size())
		);

	}



}
