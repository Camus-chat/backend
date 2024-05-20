package com.camus.backend.chat.domain.document;

import java.util.UUID;

import org.springframework.data.annotation.TypeAlias;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@FieldNameConstants
@Getter
@Setter
@TypeAlias("CommonMessage")
public class CommonMessage extends Message {
	private UUID senderId;
	@Builder.Default
	private String filteredType = "-";
	@Builder.Default
	private String _class = "CommonMessage";
}
