package com.camus.backend.chat.domain.dto;

import java.util.List;

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
public class ChatDataDto {
	private List<RedisSavedMessageBasicDto> messageList;
	private PaginationDto pagination;

}
