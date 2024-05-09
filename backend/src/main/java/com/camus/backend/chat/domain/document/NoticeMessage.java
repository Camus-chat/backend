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
public class NoticeMessage extends Message {
	private String noticeType;
	private UUID target;
	@Builder.Default
	private String _class = "NoticeMessage";
}
