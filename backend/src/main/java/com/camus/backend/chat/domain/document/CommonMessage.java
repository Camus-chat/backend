package com.camus.backend.chat.domain.document;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@FieldNameConstants
@Getter
@Setter
public class CommonMessage extends Message {
	private UUID senderId;
	private String filteredType;
	private String sentimentType;
	@Builder.Default
	private String _class = "CommonMessage";
}
