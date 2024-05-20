package com.camus.backend.chat.util;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.camus.backend.filter.util.type.FilteredType;
import com.camus.backend.filter.util.type.FilteringLevel;

@Component
public final class FilterMatch {

	public static final Map<Short, Short> FILTER_MAP = Map.of(
		FilteredType.MALICIOUS_SIMPLE.getValue(), FilteringLevel.LOW.getValue(),
		FilteredType.MALICIOUS_LAMBDA.getValue(), FilteringLevel.LOW.getValue(),

		FilteredType.MALICIOUS_CLOVA.getValue(), FilteringLevel.MIDDLE.getValue(),
		FilteredType.HATE_LAMBDA.getValue(), FilteringLevel.MIDDLE.getValue(),

		FilteredType.SPAM_CLOVA.getValue(), FilteringLevel.HIGH.getValue(),
		FilteredType.HATE_CLOVA.getValue(), FilteringLevel.HIGH.getValue()
	);

}
