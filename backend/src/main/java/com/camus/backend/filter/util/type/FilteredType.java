package com.camus.backend.filter.util.type;

import lombok.Getter;

@Getter
public enum FilteredType {
	MALICIOUS((short)0),
	MALICIOUS_SIMPLE((short)1),
	MALICIOUS_LAMBDA((short)2),
	MALICIOUS_CLOVA((short)3),
	HATE((short)100),
	HATE_LAMBDA((short)101),
	HATE_CLOVA((short)102),
	SPAM((short)200),
	SPAM_CLOVA((short)201),
	NOT_FILTERED((short)300)
	;
	private final short value;

	FilteredType(int value) {
		this.value = (short)value;
	}
	public static FilteredType fromValue(short value) {
		for (FilteredType type : FilteredType.values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new IllegalArgumentException("No FilteredType with value: " + value);
	}

	public FilteredType getBaseType() {
		short baseValue = (short)((this.value / 100) * 100);
		return fromValue(baseValue);
	}

}
