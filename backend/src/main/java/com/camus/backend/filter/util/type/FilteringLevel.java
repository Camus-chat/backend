package com.camus.backend.filter.util.type;

import lombok.Getter;

@Getter
public enum FilteringLevel {
	LOW((short)100),
	MIDDLE((short)200),
	HIGH((short)300)
	;
	private final short value;
	FilteringLevel(int value) {
		this.value = (short) value;
	}

}
