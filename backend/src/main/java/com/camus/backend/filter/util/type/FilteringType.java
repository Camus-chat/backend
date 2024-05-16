package com.camus.backend.filter.util.type;

import lombok.Getter;

@Getter
public enum FilteringType {
	SINGLE ((byte)0),
	CONTEXT ((byte)1)
	;
	private final byte value;

	FilteringType(int value) {
		this.value = (byte) value;
	}

}
