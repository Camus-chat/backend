package com.camus.backend.member.domain.dto;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.camus.backend.member.domain.document.MemberCredential;

public class CustomUserDetails implements UserDetails {

	// 맴버 정보 담는 UserDetails 객체

	private final MemberCredential memberCredential;

	public CustomUserDetails(MemberCredential memberCredential) {
		this.memberCredential = memberCredential;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return memberCredential.getRole();
			}
		});
		return collection;
	}

	@Override
	public String getPassword() {
		return memberCredential.getPassword();
	}

	@Override
	public String getUsername() {
		return memberCredential.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
