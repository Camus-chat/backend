package com.camus.backend.filter.domain.Response;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class FilteringResponse {
	UUID roomId;
}
