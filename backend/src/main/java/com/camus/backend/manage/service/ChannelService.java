package com.camus.backend.manage.service;

import static com.camus.backend.manage.util.ChannelUtil.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.camus.backend.manage.domain.document.Channel;
import com.camus.backend.manage.domain.document.ChannelList;
import com.camus.backend.manage.domain.dto.ChannelDto;
import com.camus.backend.manage.domain.dto.ChannelInfoDto;

import com.camus.backend.manage.domain.dto.ChannelListDto;

import com.camus.backend.manage.domain.dto.CreateChannelDto;
import com.camus.backend.manage.domain.repository.ChannelListRepository;
import com.camus.backend.manage.util.ManageConstants;
import com.camus.backend.member.domain.dto.CustomUserDetails;

@Service
public class ChannelService {

	private final ChannelListRepository channelListRepository;
	private final RoomService roomService;

	public ChannelService(ChannelListRepository channelListRepository, RoomService roomService) {
		this.channelListRepository = channelListRepository;
		this.roomService = roomService;
	}

	// FIXME : 사용자마다 ChannelList임시 생성로직
	// FIXME : 사용자가 가입하면 곧바로 리스트 생성해야된다.
	public void createChannelList(
		// 인증정보 보내주라

	) {

		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		ChannelList newChannelList = ChannelList.builder()
			._id(userUuid)
			.channels(new ArrayList<>())
			.build();
		channelListRepository.save(newChannelList);
	}

	// FeatureID 501-1 : 채널 생성 메서드
	public ChannelDto createChannel(
		CreateChannelDto createChannelDto
		// TODO : 사용자 인증 정보 받기
	) {

		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		// // FIXME : uuid 삭제 -> 사용자 인증 정보로 처리
		// UUID uuid = ManageConstants.tempMemUuid;

		checkChannelTitleLengthLimit(createChannelDto.getTitle());
		checkChannelContentLengthLimit(createChannelDto.getContent());
		checkChannelFilterLevel(createChannelDto.getFilterLevel());

		checkValidChannelType(createChannelDto.getType());

		Channel newChannel = Channel.builder()
			.type(createChannelDto.getType())
			.title(createChannelDto.getTitle())
			.content(createChannelDto.getContent())
			.filterLevel(createChannelDto.getFilterLevel())
			.createDate(LocalDateTime.now())
			.validDate(
				LocalDateTime.now()
					.plusMonths(ManageConstants.CHANNEL_VALID_DATE_MONTH)
			)
			.maxRooms(
				createChannelDto.getType()
					.equals(ManageConstants.CHANNEL_TYPE_PRIVATE) ?
					ManageConstants.PRIVATE_CHANNEL_MAX_ROOMS :
					ManageConstants.GROUP_CHANNEL_MAX_ROOMS)
			.build();

		if (newChannel.getType().equals(ManageConstants.CHANNEL_TYPE_GROUP)) {
			UUID newRoomId = roomService.createGroupRoomByOwnerId(newChannel.getKey(), userUuid);
			newChannel.getChatRoomList().add(newRoomId);
		}

		channelListRepository.addChannelToMemberChannels(userUuid, newChannel);
		return new ChannelDto(createChannelDto, newChannel.getKey());
	}

	// FeatureID 501-1 : 채널 리스트 반환 메서드
	public List<ChannelDto> getChannelList(
		// TODO : 사용자 인증 정보 받기
	) {
		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		ChannelList channelList = channelListRepository.getChannelListByMemberId(userUuid);

		List<ChannelDto> channelDtoList = new ArrayList<>();
		for (int i = 0; i < channelList.getChannels().size(); i++) {
			Channel channel = channelList.getChannels().get(i);
			// 유효 채널 검사
			if (!channel.getIsValid())
				continue;
			channelDtoList.add(
				new ChannelDto(channel)
			);
		}
		return channelDtoList;
	}

	// FeatureID 503-1
	public void disableChannel(
		// TODO : 사용자 인증 정보 받기
		String channelLink
	) {
		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		//UUID uuid = UUID.fromString("9f7cbe77-ee25-45df-b404-f70e8072cbfa");
		UUID uuidLink = UUID.fromString(channelLink);
		// CHECK : 채널의 유효성을 false로 변경
		channelListRepository.disableChannelByLink(uuidLink, userUuid);
	}

	// FeatureID 510-1
	public void editChannelInfo(
		// TODO : 사용자 인증 정보 받기
		@RequestBody ChannelInfoDto channelInfoDto
	) {
		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		UUID userUuid = userDetails.get_id();

		// 이게 사용자 정보
		// UUID uuid = UUID.fromString("9f7cbe77-ee25-45df-b404-f70e8072cbfa");

		checkChannelTitleLengthLimit(channelInfoDto.getTitle());
		checkChannelContentLengthLimit(channelInfoDto.getContent());
		checkChannelFilterLevel(channelInfoDto.getFilterLevel());

		channelListRepository.editChannelInfo(userUuid, channelInfoDto);
	}


}
