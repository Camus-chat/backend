package com.camus.backend.member.domain.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    private String accessToken;
    private String role;
}
