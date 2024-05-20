package com.camus.backend.filter.domain.Response;

import java.util.UUID;

import com.camus.backend.filter.domain.Request.ContextFilteringRequest;
import com.camus.backend.filter.domain.Request.SingleFilteringRequest;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = SingleFilteringResponse.class, name = "singleRes"),
	@JsonSubTypes.Type(value = ContextFilteringResponse.class, name = "contextRes"),
})
public abstract class FilteringResponse {
	UUID roomId;
}
