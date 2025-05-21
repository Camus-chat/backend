package com.camus.backend.chat.service;

import java.time.LocalDateTime;
import java.util.UUID;

import com.camus.backend.chat.domain.document.RedisSavedCommonMessage;
import com.camus.backend.chat.domain.document.RedisSavedNoticeMessage;
import com.camus.backend.chat.domain.dto.chatmessagedto.CommonMessageDto;
import com.camus.backend.chat.domain.dto.chatmessagedto.NoticeMessageDto;
import com.camus.backend.chat.domain.message.FilteredMessageToClient;
import com.camus.backend.chat.util.ChatModules;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.camus.backend.filter.service.FilterService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.chat.domain.document.NoticeMessage;
import com.camus.backend.chat.domain.dto.FilteredMessageDto;
import com.camus.backend.chat.domain.repository.RedisChatRepository;
import com.camus.backend.chat.util.ChatNoticeType;

@Service
public class RedisChatService {
	private final RedisChatRepository redisChatRepository;
	private final FilterService filterService;
	private final SimpMessagingTemplate simpMessagingTemplate;
	private final ChatModules chatModules;

		RedisChatService(RedisChatRepository redisChatRepository,
			FilterService filterService,
			SimpMessagingTemplate simpMessagingTemplate,
			ChatModules chatModules) {
		this.redisChatRepository = redisChatRepository;
		this.filterService = filterService;
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.chatModules = chatModules;
	}

	public void saveCommonMessageToRedis(
		CommonMessage commonMessage) {

		long messageId = redisChatRepository.addCommonMessage(commonMessage);
		commonMessage.setMessageId(messageId);

		// TODO : KafKa에 redis에 저장됐다 메시지 전송
//		kafkaRedisChatProducer.sendCommonMessage(commonMessage);
		simpMessagingTemplate.convertAndSend(convertTopic(commonMessage.getRoomId().toString()),
				new CommonMessageDto(new RedisSavedCommonMessage(commonMessage)));
		try {
			filterService.predict(new SingleFilteringRequest(commonMessage));
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void saveFilteredMessageToRedis(
		FilteredMessageDto filteredMessageDto) {
		// WOO TODO : 필터링 저장 로직
		if (redisChatRepository.addFilteredType(filteredMessageDto)) {
			// WOO TODO : KafKa에 redis에 저장됐다 메시지 전송
//			kafkaRedisChatProducer.sendFilterMessage(
//				filteredMessageDto
//			);
			simpMessagingTemplate.convertAndSend(convertTopic(filteredMessageDto.getRoomId().toString()),
					new FilteredMessageToClient(filteredMessageDto));
//			System.out.println("kafka success");
		}

	}

	public void createChatRoomNotice(String roomId, UUID userId) {
		NoticeMessage firstNoticeMessage = NoticeMessage.builder()
			.roomId(UUID.fromString(roomId))
			.createdDate(LocalDateTime.now())
			.content(ChatNoticeType.ENTER_ROOM.getNoticeContent())
			.target(userId)
			.noticeType(ChatNoticeType.ENTER_ROOM.getNoticeType())
			.build();

		redisChatRepository.addNoticeMessage(firstNoticeMessage);
		redisChatRepository.createStreamConsumerGroup(roomId
			, userId);

		// TODO : KafKa에 redis에 저장됐다 메시지 전송
//		kafkaRedisChatProducer.sendNoticeMessage(firstNoticeMessage);
		simpMessagingTemplate.convertAndSend(convertTopic(roomId),
				new NoticeMessageDto(new RedisSavedNoticeMessage(firstNoticeMessage)));
	}

	public void newUserEnterRoomNotice(String roomId, UUID userId) {
		NoticeMessage newUserEnterRoomNotice = NoticeMessage.builder()
			.roomId(UUID.fromString(roomId))
			.createdDate(LocalDateTime.now())
			.content(ChatNoticeType.ENTER_ROOM.getNoticeContent())
			.target(userId)
			.noticeType(ChatNoticeType.ENTER_ROOM.getNoticeType())
			.build();

		//redis에 저장
		redisChatRepository.addNoticeMessage(newUserEnterRoomNotice);
		//사용자 - consumer에 추가
		redisChatRepository.updateStreamConsumerGroup(roomId
			, userId, redisChatRepository.getLatestRedisMessageId(roomId));

//		kafkaRedisChatProducer.sendNoticeMessage(newUserEnterRoomNotice);
		simpMessagingTemplate.convertAndSend(convertTopic(roomId),
				new NoticeMessageDto(new RedisSavedNoticeMessage(newUserEnterRoomNotice)));
	}

	private String convertTopic(String roomId){
		return "/sub/"+roomId;
	}
}
