package com.camus.backend.manage.util;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomEntryManager {
	private boolean check;
	private UUID roomId;
}
