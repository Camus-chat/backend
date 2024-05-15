package com.camus.backend.chat.domain.document;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@SuperBuilder
@FieldNameConstants
@Getter
@Setter
@ToString
@TypeAlias("NoticeMessage")
public class NoticeMessage extends Message {
	private String noticeType;
	private UUID target;
	@Builder.Default
	private String _class = "NoticeMessage";
}
