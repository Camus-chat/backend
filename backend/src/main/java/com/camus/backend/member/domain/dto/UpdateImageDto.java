package com.camus.backend.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateImageDto {
	private MultipartFile newProfileImage;
}
