package com.camus.backend.member.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCredentialDto {
	private String username;
	private String password;
	private String nickname;
	@JsonProperty("isEnterprise") //boolean 값에 대해 is로 시작하면 매칭 안됨
	private boolean isEnterprise;
}
