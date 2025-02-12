package com.camus.backend.member.domain.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountProfileDto {
    private UUID myUuid;
    private String nickname;
    private String profileLink;
    private String role;
}
