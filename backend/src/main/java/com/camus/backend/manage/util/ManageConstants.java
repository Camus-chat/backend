package com.camus.backend.manage.util;

import java.util.UUID;

public final class ManageConstants {

	public static final int CHANNEL_VALID_DATE_MONTH = 6;

	// Title, Content 길이 제한
	public static final int MAX_CHANNEL_TITLE_LENGTH = 8;
	public static final int MIN_CHANNEL_TITLE_LENGTH = 1;
	public static final int MAX_CHANNEL_CONTENTS_LENGTH = 100;
	public static final int MIN_CHANNEL_CONTENTS_LENGTH = 0;

	// Channel 생성 방 수
	public static final int PRIVATE_CHANNEL_MAX_ROOMS = 10000;
	public static final int GROUP_CHANNEL_MAX_ROOMS = 1;

	// Channel 필터링 조건
	public static final int CHANNEL_FILTER_MIN_LEVEL = 100;
	public static final int CHANNEL_FILTER_MIDDLE_LEVEL = 200;
	public static final int CHANNEL_FILTER_MAX_LEVEL = 300;

	public static final int PRIVATE_ROOM_MAX_MEMBERS = 2;
	public static final int GROUP_ROOM_MAX_MEMBERS = 3000;

	public static final String CHANNEL_TYPE_PRIVATE = "private";
	public static final String CHANNEL_TYPE_GROUP = "group";

	// FIXME : 임시 사용자 정보
	public static final UUID tempMemUuid = UUID.fromString("9f7cbe77-ee25-45df-b404-f70e8072cbfa");

	private ManageConstants() {
	}
}
