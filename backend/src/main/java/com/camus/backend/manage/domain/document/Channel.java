package com.camus.backend.manage.domain.document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@Document
@ToString
@TypeAlias("channel")
public class Channel {
	@Builder.Default
	@Id
	private UUID key = UUID.randomUUID();

	@Builder.Default
	@Indexed
	private UUID link = UUID.randomUUID();
	private String type;
	private String title;
	// CHECK : content 100자?
	private String content;
	@Builder.Default
	private Boolean isValid = true;
	private LocalDateTime validDate;
	@Builder.Default
	private List<UUID> chatRoomList = new ArrayList<>();
	private Integer maxMembers;
	@Builder.Default
	private Boolean isClose = false;
	private Integer filterLevel;
}
