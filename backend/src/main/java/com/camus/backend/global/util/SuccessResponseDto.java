package com.camus.backend.global.util;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuccessResponseDto {
	private String code;
	private String description;

	@Builder
	public SuccessResponseDto(SuccessCode successCode) {
		this.code = successCode.getOkResponse();
		this.description = successCode.getSuccessMessage();
	}

}
