package com.camus.backend.filter.util.type;

import lombok.Getter;

@Getter
public enum ContextFilteringType {
	MALICIOUS("욕설/비하/모욕"),
	HATE("성희롱/혐오"),
	SPAM("도배/홍보"),
	NOT_FILTERED("중립");

	// description을 반환하는 메서드
	private final String value;

	// 생성자를 private으로 선언하여, enum 외부에서 인스턴스화를 방지합니다.
	ContextFilteringType(String value) {
		this.value = value;
	}
	public static ContextFilteringType fromString(String name) {
		for (ContextFilteringType type : ContextFilteringType.values()) {
			if (type.value.equals(name)) {
				return type;
			}
		}
		return null; // 또는 기본값 반환
	}
}

