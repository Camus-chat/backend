package com.camus.backend.manage.domain.document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Channel {
	@Id
	private UUID _id;

	@Indexed
	private String link;
	private String type;
	private String title;
	// CHECK : content 100자?
	private String content;
	private Boolean isValid;
	private LocalDateTime validDate;
	private List<UUID> chatRoomList;
	private Integer maxMembers;
	private Boolean isClose;
	private Integer filterLevel;
}
