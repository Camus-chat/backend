package com.camus.backend.member.domain.dto;

import org.springframework.web.multipart.MultipartFile;

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
public class GuestMemberCredentialDto {
	private String username;
	private String password;
	private String nickname;
	private String profileImageColor;
}
