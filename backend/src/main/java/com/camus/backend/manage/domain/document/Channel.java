package com.camus.backend.manage.domain.document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@Document
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("Channel")
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
	private LocalDateTime createDate;
	private LocalDateTime validDate;
	@Builder.Default
	private List<UUID> chatRoomList = new ArrayList<>();
	private Integer maxRooms;
	@Builder.Default
	private Boolean isClose = false;
	private Integer filterLevel;

}
