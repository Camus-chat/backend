package com.camus.backend.chat.domain.dto;

import com.camus.backend.chat.domain.document.CommonMessage;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
public class RedisSavedCommonMessageDto extends RedisSavedMessageBasicDto {
	private String filteredType;
	private UUID senderId;

	public RedisSavedCommonMessageDto() {
		super();
	}

	public RedisSavedCommonMessageDto(CommonMessage commonMessage) {
		super(commonMessage);
		this.filteredType = commonMessage.getFilteredType();
		this.senderId = commonMessage.getSenderId();
	}

	@Override
	public String toString() {
		return super.toString() + "filteredType : " + filteredType + "\n";
	}
}
