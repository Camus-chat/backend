package com.camus.backend.member.domain.document.MemberProfile;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Document(collection = "member_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class MemberProfile {
	@Id
	private String _id;
}
