package com.camus.backend.member.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class MemberCredentialDto {
	private String username;
	private String password;
	private String input1;
	private String input2;
	// private String role;
}
