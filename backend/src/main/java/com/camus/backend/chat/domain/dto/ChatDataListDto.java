package com.camus.backend.chat.domain.dto;

import java.util.List;

import com.camus.backend.chat.domain.dto.chatmessagedto.MessageBasicDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
public class ChatDataListDto {
	private List<MessageBasicDto> messageList;
	private PaginationDto paginationDto;
}
