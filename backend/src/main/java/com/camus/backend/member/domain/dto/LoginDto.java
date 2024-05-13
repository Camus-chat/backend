package com.camus.backend.member.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class LoginDto {
	private String authorization;
}
