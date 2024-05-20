package com.camus.backend.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class B2BMemberCredentialDto {
	private String username;
	private String password;
	private String companyName;
	private String companyEmail;
}
