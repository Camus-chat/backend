package com.camus.backend.member.domain.document.MemberProfile;

import org.springframework.data.annotation.TypeAlias;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@TypeAlias("b2bProfile")
@Getter
@Setter
@Document(collection = "b2b_profile")

public class B2BProfile extends MemberProfile {
	private String companyName;
	private String companyEmail;
}
