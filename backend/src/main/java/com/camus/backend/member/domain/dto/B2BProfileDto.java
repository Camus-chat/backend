package com.camus.backend.member.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class B2BProfileDto {
	private String companyName;
	private String companyEmail;
}
