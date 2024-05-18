package com.camus.backend.filter.domain.Request;

import java.util.UUID;

import com.camus.backend.filter.util.type.FilteringLevel;
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
	@JsonSubTypes.Type(value = SingleFilteringRequest.class, name = "singleReq"),
	@JsonSubTypes.Type(value = ContextFilteringRequest.class, name = "contextReq")
})
public abstract class FilteringRequest {
	UUID roomId;
	FilteringLevel filteringLevel;
}
