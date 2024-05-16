package com.camus.backend.filter.util.type;

import lombok.Getter;

@Getter
public enum SingleFilteringType {
	MALICIOUS(0),
	HATE(1),
	SPAM(2),
	NOT_FILTERED(3)
	;

	private final int value;

	// 생성자를 private으로 선언하여, enum 외부에서 인스턴스화를 방지합니다.
	SingleFilteringType(int value) {
		this.value = value;
	}

	public static SingleFilteringType fromValue(int value) {
		for (SingleFilteringType type : values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		throw new IllegalArgumentException("No SingleFilteringType with value: " + value);
	}
}
