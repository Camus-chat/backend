package com.camus.backend.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class PaginationDto {
	private String nextMessageTimeStamp;
	private int size;
}
