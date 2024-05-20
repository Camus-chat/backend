package com.camus.backend.member.domain.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class B2BProfileDto {
	private UUID myUuid;
	private String companyName;
	private String companyEmail;
}
