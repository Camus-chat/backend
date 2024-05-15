package com.camus.backend.manage.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChannelStatus {
	private boolean valid;
	private String type;
	private String title;
}
