package com.camus.backend.manage.domain.document;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(collection = "channel_list")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelList {
	@Id
	@Field("_id")
	private UUID _id;
	@Field("channels")
	private List<Channel> channels;

}