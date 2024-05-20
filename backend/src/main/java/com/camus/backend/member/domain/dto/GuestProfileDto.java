package com.camus.backend.member.domain.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GuestProfileDto {
	private UUID myUuid;
	private String nickname;
	private String profileImageColor;
}
