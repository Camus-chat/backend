package com.camus.backend.member.domain.document.MemberProfile;

import java.util.UUID;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "member_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public abstract class MemberProfile {
	@Id
	private UUID _id;
	private String nickname;
}
