package com.camus.backend.chat.domain.message;

import com.camus.backend.chat.domain.dto.FilteredMessageDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilteredMessageToClient {

	private String type;
	private String roomId;
	private String messageId;
	private int filteredLevel;
	private Short filteredType;
	private String createdDate;

	public FilteredMessageToClient(FilteredMessageDto filteredMessageDto) {
		this.type = "FilteredMessage";
		this.roomId = filteredMessageDto.getRoomId().toString();
		this.messageId = "filtered:" + String.valueOf(filteredMessageDto.getMessageId());
		this.filteredLevel = filteredMessageDto.getFilteredLevel();
		this.filteredType = filteredMessageDto.getFilteredType();
		this.createdDate = filteredMessageDto.getCreatedDate().toString();
	}
}
