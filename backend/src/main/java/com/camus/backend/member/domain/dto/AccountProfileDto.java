package com.camus.backend.member.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class AccountProfileDto {
    private UUID uuid;
    private String username;
    private String nickname;
    private String profileLink;
    private String role;
}
