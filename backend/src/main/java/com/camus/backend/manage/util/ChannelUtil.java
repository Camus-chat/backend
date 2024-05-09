package com.camus.backend.manage.util;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;

public class ChannelUtil {

	private ChannelUtil() {

	}

	public static void checkChannelTitleLengthLimit(String title) {
		if (title.length() > ManageConstants.MAX_CHANNEL_TITLE_LENGTH) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_CHANNEL_TITLE);
		}
		if (title.length() < ManageConstants.MIN_CHANNEL_TITLE_LENGTH) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_CHANNEL_TITLE);
		}
	}

	public static void checkChannelContentLengthLimit(String content) {
		if (content.length() > ManageConstants.MAX_CHANNEL_CONTENTS_LENGTH) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_CHANNEL_CONTENT);
		}
	}

	public static void checkChannelFilterLevel(int filterLevel) {
		if (filterLevel < ManageConstants.CHANNEL_FILTER_MIN_LEVEL
			|| filterLevel > ManageConstants.CHANNEL_FILTER_MAX_LEVEL) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_CHANNEL_FILTER_LEVEL);
		}
	}

	public static void checkValidChannelType(String type) {
		if (!type.equals(ManageConstants.CHANNEL_TYPE_PRIVATE) && !type.equals(ManageConstants.CHANNEL_TYPE_GROUP)) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_CHANNEL_TYPE);
		}
	}
}
