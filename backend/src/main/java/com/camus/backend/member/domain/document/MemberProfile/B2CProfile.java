package com.camus.backend.member.domain.document.MemberProfile;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@TypeAlias("b2cProfile")
public class B2CProfile extends MemberProfile {
	private String nickname;
	// CHECK : 왜 이걸 빼면 안되는가. Field Mapping을 강제로 해주고 있긴 함ㅠ
	@Field("profile_link")
	private String profileLink;
}
