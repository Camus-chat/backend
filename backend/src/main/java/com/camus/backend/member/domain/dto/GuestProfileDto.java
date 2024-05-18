package com.camus.backend.member.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class GuestProfileDto {
	private String nickname;
	private String profileImageColor;
}
