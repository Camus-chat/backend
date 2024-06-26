package com.camus.backend.filter.domain.Request;

import com.camus.backend.chat.domain.document.CommonMessage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SingleFilteringRequest extends FilteringRequest{
	private SimpleMessage simpleMessage;

	public SingleFilteringRequest(CommonMessage commonMessage){
		setRoomId(commonMessage.getRoomId());
		simpleMessage = new SimpleMessage(commonMessage);
	}
}
