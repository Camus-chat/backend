package com.camus.backend.filter.util.type;

import lombok.Getter;

@Getter
public enum FilteredType {
	MALICIOUS_SIMPLE((byte)0),
	MALICIOUS_LAMBDA((byte)1),
	MALICIOUS_CLOVA((byte)2),
	HATE_LAMBDA((byte)3),
	HATE_CLOVA((byte)4),
	SPAM((byte)5),
	NOT_FILTERED((byte)6)
	;
	private final byte value;

	FilteredType(int value) {
		this.value = (byte)value;
	}

	//String type으로 변환 필요?
	//clova, lambda 분류해야하나?
}
