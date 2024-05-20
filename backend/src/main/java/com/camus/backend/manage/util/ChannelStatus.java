package com.camus.backend.manage.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ChannelStatus {
	private boolean valid;
	private String type;
	private String title;
	private int filteredLevel;
	private UUID key;
	private UUID ownerId;
}
