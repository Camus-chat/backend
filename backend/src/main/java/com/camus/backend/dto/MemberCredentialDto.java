package com.camus.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MemberCredentialDto {
	private String username;
	private String password;
	private String role;
}
