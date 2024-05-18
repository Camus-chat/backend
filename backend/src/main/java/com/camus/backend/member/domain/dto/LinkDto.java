package com.camus.backend.member.domain.dto;

import com.camus.backend.member.domain.document.MemberProfile.GuestProfile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkDto {
	private GuestProfile guestProfile;

}
