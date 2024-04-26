package com.camus.backend.manage.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.camus.backend.manage.domain.document.Channel;
import com.camus.backend.manage.domain.dto.ChannelDto;
import com.camus.backend.manage.domain.dto.CreateChannelDto;
import com.camus.backend.manage.domain.repository.ChannelListRepository;

@Service
public class ChannelService {

	private final ChannelListRepository channelListRepository;

	public ChannelService(ChannelListRepository channelListRepository) {
		this.channelListRepository = channelListRepository;
	}

	public ChannelDto createChannel(CreateChannelDto createChannelDto
		// TODO : 사용자 인증 정보 받기
	) {
		Channel newChannel = Channel.builder()
			._id(UUID.randomUUID())
			.type(createChannelDto.getType())
			.title(createChannelDto.getTitle())
			.content(createChannelDto.getContent())
			.filterLevel(createChannelDto.getFilterLevel())
			// CHECK : 채팅의 link 생성
			.link("http://localhost:8080/channel/" + UUID.randomUUID())
			.isValid(true)
			.validDate(LocalDateTime.now())
			.chatRoomList(new ArrayList<>())
			// TODO : 정책 정의 하고 숫자 final로 수정
			.maxMembers(createChannelDto.getType().equals("private") ? 2 : 3000)
			.isClose(false)
			.build();

		// TODO : 본인 리스트에 추가
		String uuid = "0000";

		return new ChannelDto(createChannelDto, newChannel.getLink());
	}

}
