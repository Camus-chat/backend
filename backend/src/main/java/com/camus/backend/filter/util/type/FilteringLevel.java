package com.camus.backend.filter.util.type;

import lombok.Getter;

@Getter
public enum FilteringLevel {
	LOW((byte)0),
	MIDDLE((byte)1),
	HIGH((byte)2)
	;
	private final byte value;
	FilteringLevel(int value) {
		this.value = (byte) value;
	}

}
