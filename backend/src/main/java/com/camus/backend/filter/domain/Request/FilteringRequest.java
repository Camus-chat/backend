package com.camus.backend.filter.domain.Request;

import java.util.UUID;

import com.camus.backend.filter.util.type.FilteringLevel;
import com.camus.backend.filter.util.type.FilteringType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class FilteringRequest {
	FilteringLevel filteringLevel;
	UUID roomId;
}
