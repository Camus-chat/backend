package com.camus.backend.chat.domain.dto.chatmessagedto;

import java.util.UUID;

import com.camus.backend.chat.domain.document.RedisSavedCommonMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonMessageDto extends MessageBasicDto {
	private String filteredLevel;
	private UUID senderId;

	public CommonMessageDto(RedisSavedCommonMessage redisSavedCommonMessage) {
		super(redisSavedCommonMessage);
		this.filteredLevel = redisSavedCommonMessage.getFilteredType();
	}
}
