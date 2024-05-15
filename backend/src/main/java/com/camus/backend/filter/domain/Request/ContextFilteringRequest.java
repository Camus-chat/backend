package com.camus.backend.filter.domain.Request;

import java.util.ArrayList;
import java.util.List;

import com.camus.backend.chat.domain.document.CommonMessage;
import com.camus.backend.filter.util.type.FilteringLevel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContextFilteringRequest extends FilteringRequest{
	List<UserMessage> userMessages;
	public ContextFilteringRequest(List<CommonMessage> commonMessages, FilteringLevel filteringLevel){
		setRoomId(commonMessages.get(0).getRoomId());
		setFilteringLevel(filteringLevel);
		userMessages = new ArrayList<>();
		for (CommonMessage commonMessage: commonMessages){
			userMessages.add(new UserMessage(commonMessage));
		}
	}
}
