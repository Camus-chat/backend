package com.camus.backend.member.domain.dto;

import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateImageDto {
	private MultipartFile profileImage;
}
