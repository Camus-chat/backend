package com.camus.backend.member.domain.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class B2CUpdateImageDto {
	private MultipartFile newProfileImage;
}
