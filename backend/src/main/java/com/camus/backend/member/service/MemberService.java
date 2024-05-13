package com.camus.backend.member.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.global.util.GuestUtil;
import com.camus.backend.global.util.SuccessCode;
import com.camus.backend.global.util.SuccessResponseDto;
import com.camus.backend.member.domain.document.MemberCredential;
import com.camus.backend.member.domain.document.MemberProfile.B2BProfile;
import com.camus.backend.member.domain.document.MemberProfile.B2CProfile;
import com.camus.backend.member.domain.document.MemberProfile.GuestProfile;
import com.camus.backend.member.domain.document.MemberProfile.MemberProfile;
import com.camus.backend.member.domain.dto.B2CProfileDto;
import com.camus.backend.member.domain.dto.B2CUpdateNicknameDto;
import com.camus.backend.member.domain.dto.MemberCredentialDto;
import com.camus.backend.member.domain.repository.MemberCredentialRepository;
import com.camus.backend.member.domain.repository.MemberProfileRepository;

@Service
public class MemberService {

	private final MemberCredentialRepository memberCredentialRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final MemberProfileRepository memberProfileRepository;

	public MemberService(MemberCredentialRepository memberCredentialRepository,
		BCryptPasswordEncoder bCryptPasswordEncoder, MemberProfileRepository memberProfileRepository) {
		this.memberCredentialRepository = memberCredentialRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.memberProfileRepository = memberProfileRepository;
	}

	// 회원가입(db에 넣기) 후 프론트에 아이디, 비번 보내기
	public void signUp(MemberCredentialDto memberCredentialDto, String role){
		String username= memberCredentialDto.getUsername();
		String password= memberCredentialDto.getPassword();

		// // 이미 사용되는 username이면 생성 못함
		// Boolean isExist=memberCredentialRepository.existsByUsername(username);
		// if(isExist){
		// 	return false;
		// }

		// 비밀번호 암호화
		String encodedPassword = bCryptPasswordEncoder.encode(password);

		MemberCredential newMemberCredential=MemberCredential.builder()
			._id(UUID.randomUUID())
			.username(username)
			.password(encodedPassword)
			.role(role)
			.loginTime(LocalDateTime.now())
			.build();

		memberCredentialRepository.save(newMemberCredential);

		MemberProfile profile;
		if ("b2b".equals(role)) {
			profile = new B2BProfile();
			String companyName=memberCredentialDto.getInput1();
			String companyEmail=memberCredentialDto.getInput2();
			((B2BProfile) profile).setCompanyName(companyName);
			((B2BProfile) profile).setCompanyEmail(companyEmail);
		} else if ("b2c".equals(role)) {
			profile = new B2CProfile();
			String nickname=memberCredentialDto.getInput1();
			String profileLink=memberCredentialDto.getInput2(); // 있다고 가정
			((B2CProfile) profile).setNickname(nickname);
			((B2CProfile) profile).setProfileLink(profileLink);
		} else {
			profile = new GuestProfile();
			String nickname= GuestUtil.makeNickname();
			String profilePalette=GuestUtil.chooseColorPalette();
			((GuestProfile) profile).setNickname(nickname);
			((GuestProfile) profile).setProfilePalette(profilePalette);
		}

		// 프로필 ID 설정
		profile.set_id(newMemberCredential.get_id());

		// 프로필 저장
		memberProfileRepository.save(profile);

		// List<String> credentials = new ArrayList<>();
		// credentials.add(username);
		// credentials.add(password);

		// 시간은 한국 시간으로 정상적으로 찍히네
		// System.out.println(memberCredentialRepository.findByUsername(username).getLoginTime());

		// 저장 후 username과 암호화된 password를 리스트로 반환
		return;
	}

	// id가 db에 있는지 체크. 있으면 false 없으면 true
	public boolean idCheck(String username){
		return !memberCredentialRepository.existsByUsername(username);
	}

}
