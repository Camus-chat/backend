package com.camus.backend.filter.domain.Response;

import java.util.ArrayList;
import java.util.List;

import com.camus.backend.filter.domain.Request.ContextFilteringRequest;
import com.camus.backend.filter.util.type.FilteredType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContextFilteringResponse extends FilteringResponse{
	List<FilteredMessage> filteredMessages;
	public ContextFilteringResponse(ContextFilteringRequest request, FilteredType[] filteredTypes){
		setRoomId(roomId);
		filteredMessages = new ArrayList<>();
		for (int i=0; i<request.getUserMessages().size(); i++){
			filteredMessages.add(new FilteredMessage(request.getUserMessages().get(i)
				.getId(), filteredTypes[i]));
		}
	}
}
