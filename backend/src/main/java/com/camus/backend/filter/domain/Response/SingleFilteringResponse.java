package com.camus.backend.filter.domain.Response;

import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.camus.backend.filter.util.type.FilteredType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SingleFilteringResponse extends FilteringResponse {
	FilteredMessage filteredMessage;

	public SingleFilteringResponse(SingleFilteringRequest request, FilteredType filteredType) {
		setRoomId(request.getRoomId());
		filteredMessage = new FilteredMessage(request.getSimpleMessage().getId(), filteredType,
			request.getSimpleMessage().getCreatedDate());
	}
}
