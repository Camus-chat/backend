package com.camus.backend.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.global.util.SuccessCode;
import com.camus.backend.global.util.SuccessResponseDto;
import com.camus.backend.member.domain.document.MemberCredential;
import com.camus.backend.member.domain.document.MemberProfile.B2CProfile;
import com.camus.backend.member.domain.dto.B2CProfileDto;
import com.camus.backend.member.domain.dto.B2CUpdateNicknameDto;
import com.camus.backend.member.domain.repository.MemberCredentialRepository;
import com.camus.backend.member.domain.repository.MemberProfileRepository;

@Service
public class MemberService {

	private final MemberCredentialRepository memberCredentialRepository;
	private final MemberProfileRepository memberProfileRepository;

	public MemberService(MemberCredentialRepository memberCredentialRepository,
		MemberProfileRepository memberProfileRepository) {
		this.memberCredentialRepository = memberCredentialRepository;
		this.memberProfileRepository = memberProfileRepository;
	}

	public MemberCredential save(MemberCredential credentials) {
		return memberCredentialRepository.save(credentials);
	}

	// FeatureID 104-1
	public B2CProfileDto readMemberBySelf(
		// TODO : 여기서 인증 클래스를 받아야 함.
	) {
		//TODO : 여기서 Id 값을 넣어줘야됨.
		Optional<B2CProfile> memberProfile = memberProfileRepository.findProfileById("0000", B2CProfile.class);

		return memberProfile.map(profile -> B2CProfileDto.builder()
				.nickname(profile.getNickname())
				.profileLink(profile.getProfileLink())
				.build())
			// CHECK : 오류처리방식 확인
			.orElseThrow(() -> new CustomException(ErrorCode.NOTFOUND_USER));

	}

	// FeatureID 105-1

	public SuccessResponseDto updateNicknameBySelf(
		// TODO : 여기서 인증 클래스를 받아야 함.
		B2CUpdateNicknameDto b2CUpdateNicknameDto
	) {
		// TODO : uuid 삭제
		String uuid = "0000";
		B2CProfile memberProfile = memberProfileRepository.findProfileById(uuid, B2CProfile.class)
			.orElseThrow(() -> new CustomException(ErrorCode.NOTFOUND_USER));

		memberProfile.setNickname(b2CUpdateNicknameDto.getNewNickname());
		memberProfileRepository.save(memberProfile);

		return SuccessResponseDto.builder().successCode(SuccessCode.NICKNAME_EDIT).build();

	}
}
