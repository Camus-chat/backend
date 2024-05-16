package com.camus.backend.filter.domain.Response;

import com.camus.backend.filter.domain.Request.ContextFilteringRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContextCountingResponse extends FilteringResponse{
	int count;
	public ContextCountingResponse(ContextFilteringRequest request, int count){
		setRoomId(request.getRoomId());
		this.count = count;
	}
}
