package com.camus.backend.manage.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.camus.backend.manage.domain.document.Channel;
import com.camus.backend.manage.domain.document.ChannelList;
import com.camus.backend.manage.domain.dto.ChannelDto;
import com.camus.backend.manage.domain.dto.ChannelListDto;
import com.camus.backend.manage.domain.dto.CreateChannelDto;
import com.camus.backend.manage.domain.repository.ChannelListRepository;

@Service
public class ChannelService {

	private final ChannelListRepository channelListRepository;

	public ChannelService(ChannelListRepository channelListRepository) {
		this.channelListRepository = channelListRepository;
	}

	// FIXME : 사용자마다으 ㅣChannelList임시 생성
	public void createChannelList() {
		UUID uuid = UUID.fromString("9f7cbe77-ee25-45df-b404-f70e8072cbfa");

		ChannelList newChannelList = ChannelList.builder()
			._id(uuid)
			.channels(new ArrayList<>())
			.build();
		channelListRepository.save(newChannelList);
	}

	// FeatureID 501-1 : 채널 생성 메서드
	public ChannelDto createChannel(CreateChannelDto createChannelDto
		// TODO : 사용자 인증 정보 받기
	) {

		// FIXME : uuid 삭제 -> 사용자 인증 정보로 처리
		UUID uuid = UUID.fromString("9f7cbe77-ee25-45df-b404-f70e8072cbfa");

		Channel newChannel = Channel.builder()
			.type(createChannelDto.getType())
			.title(createChannelDto.getTitle())
			.content(createChannelDto.getContent())
			.filterLevel(createChannelDto.getFilterLevel())
			.validDate(LocalDateTime.now())
			// TODO : 정책 정의 하고 숫자 final로 수정
			.maxMembers(createChannelDto.getType().equals("private") ? 2 : 3000)
			.build();

		channelListRepository.addChannelToMemberChannels(uuid, newChannel);
		return new ChannelDto(createChannelDto, newChannel.getLink());
	}

	// FeatureID 501-1 : 채널 리스트 반환 메서드
	public ChannelListDto getChannelList(
		// TODO : 사용자 인증 정보 받기
	) {
		UUID uuid = UUID.fromString("9f7cbe77-ee25-45df-b404-f70e8072cbfa");
		// CHECK : 본인 리스트에서 채널 리스트 가져오기
		return new ChannelListDto(channelListRepository.getChannelListByMemberId(uuid));
		
	}

	// FeatureID 503-1
	public void disableChannel(
		// TODO : 사용자 인증 정보 받기
		String channelLink
	) {
		UUID uuid = UUID.fromString("9f7cbe77-ee25-45df-b404-f70e8072cbfa");
		// CHECK : 채널의 유효성을 false로 변경
		channelListRepository.disableChannelByLink(channelLink, uuid);
	}

}
