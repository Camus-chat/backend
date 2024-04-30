package com.camus.backend.manage.domain.document;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(collection = "ChannelList")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelList {
	@Id
	private UUID _id;
	private List<Channel> channels;

}