package com.camus.backend.manage.domain.document;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "channel_list")
public class ChannelList {
	@Id
	private UUID member_id;
	private List<Channel> channels;
}