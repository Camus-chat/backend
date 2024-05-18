package com.camus.backend.member.domain.document.MemberProfile;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TypeAlias("guestProfile")
@Document(collection = "guest_profile")
public class GuestProfile extends MemberProfile{
	private String nickname;
	private String profilePalette;
}
