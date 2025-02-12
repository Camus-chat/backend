package com.camus.backend.member.domain.document.MemberProfile;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("accountProfile")
@Document(collection = "accountProfile")
public class AccountProfile extends MemberProfile {
    private String role;
    private String profileLink;
}
