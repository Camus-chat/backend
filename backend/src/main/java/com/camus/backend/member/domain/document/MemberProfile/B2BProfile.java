package com.camus.backend.member.domain.document.MemberProfile;

import org.springframework.data.annotation.TypeAlias;

@TypeAlias("b2bProfile")
public class B2BProfile extends MemberProfile {
	private String companyName;
	private String companyEmail;
}
