package com.camus.backend.chat.domain.dto;

import com.camus.backend.chat.domain.document.CommonMessage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RedisSavedCommonMessageDto extends RedisSavedMessageBasicDto {
	private String filteredType;

	public RedisSavedCommonMessageDto() {
		super();
	}

	public RedisSavedCommonMessageDto(CommonMessage commonMessage) {
		super(commonMessage);
		this.filteredType = commonMessage.getFilteredType();
	}

	@Override
	public String toString() {
		return super.toString() + "filteredType : " + filteredType + "\n";
	}
}
