package com.camus.backend.member.service;

import static com.camus.backend.global.util.GuestUtil.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import com.camus.backend.member.domain.document.MemberProfile.*;
import com.camus.backend.member.domain.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;

import com.camus.backend.global.util.GuestUtil;
import com.camus.backend.manage.service.ChannelService;
import com.camus.backend.member.domain.document.MemberCredential;

import com.camus.backend.member.domain.repository.MemberCredentialRepository;
import com.camus.backend.member.domain.repository.MemberProfileRepository;

@Service
public class MemberService {

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.s3.base-url}")
	private String baseUrl;

	private final MemberCredentialRepository memberCredentialRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final MemberProfileRepository memberProfileRepository;
	private final AmazonS3Client amazonS3Client;
	private final ChannelService channelService;


	public MemberService(MemberCredentialRepository memberCredentialRepository,
		BCryptPasswordEncoder bCryptPasswordEncoder, MemberProfileRepository memberProfileRepository,
		AmazonS3Client amazonS3Client, ChannelService channelService) {
		this.memberCredentialRepository = memberCredentialRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.memberProfileRepository = memberProfileRepository;
		this.amazonS3Client = amazonS3Client;
		this.channelService = channelService;
	}




	// 회원가입(db에 넣기) 후 프론트에 아이디, 비번 보내기
	public void memberSignUp(MemberCredentialDto memberCredentialDto) {
		String username = memberCredentialDto.getUsername();
		String password = memberCredentialDto.getPassword();

		// 이메일 유효성 검사
		if (username.length() < 10 || username.length() > 50 || invalidEmail(username)) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_EMAIL);
		}

		if (memberCredentialRepository.existsByUsername(username))
		{
			throw new CustomException(ErrorCode.CONFLICT_EMAIL);
		}

		// password 유효성 검사
		if (password == null || password.trim().isEmpty()) {
			throw new CustomException(ErrorCode.MISSING_PARAMETER_PW);
		}

		// 비밀번호 암호화
		String encodedPassword = bCryptPasswordEncoder.encode(password);

		// 사용자 uuid 생성
		UUID memberUuid = UUID.randomUUID();

		MemberCredential newMemberCredential = MemberCredential.builder()
			._id(memberUuid)
			.username(username)
			.password(encodedPassword)
			.role((memberCredentialDto.isEnterprise()?"b2b":"b2c"))
			.loginTime(LocalDateTime.now())
			.build();

		memberCredentialRepository.save(newMemberCredential);

		// 채널리스트 생성
		channelService.createChannelList(memberUuid);

		AccountProfile memberProfile = AccountProfile.builder()
				._id(newMemberCredential.get_id())
				.nickname(memberCredentialDto.getNickname())
				.role((memberCredentialDto.isEnterprise()?"b2b":"b2c"))
				.build();

		// // guest 로직
		// memberProfile = new GuestProfile();
		// 프로필 ID 설정
		// memberProfile.set_id(newMemberCredential.get_id());
		// String nickname = GuestUtil.makeNickname();
		// String profilePalette = GuestUtil.chooseColorPalette();
		// ((GuestProfile)memberProfile).setNickname(nickname);
		// ((GuestProfile)memberProfile).setProfilePalette(profilePalette);

		// // 프로필 ID 설정
		// memberProfile.set_id(newMemberCredential.get_id());

		// 프로필 저장
		memberProfileRepository.save(memberProfile);

	}

	// guest 회원가입
	// 토큰 닉네임 프사 주기
	public GuestSignUpDto guestSignUp(MemberCredentialDto memberCredentialDto){

		String username = memberCredentialDto.getUsername();
		String password = memberCredentialDto.getPassword();

		// username 받았는지 검사
		if (username == null || username.trim().isEmpty()) {
			throw new CustomException(ErrorCode.MISSING_PARAMETER_ID);
		}

		// username 유효성 검사
		if (username.length() < 5 || username.length() > 20 || !Pattern.matches("^[A-Za-z0-9\\-_]+$", username)) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_ID);
		}

		// password 받았는지 검사
		if (password == null || password.trim().isEmpty()) {
			throw new CustomException(ErrorCode.MISSING_PARAMETER_PW);
		}

		// 비밀번호 암호화
		String encodedPassword = bCryptPasswordEncoder.encode(password);

		// 사용자 uuid 생성
		UUID memberUuid = UUID.randomUUID();

		String guestRole = "guest";

		MemberCredential newMemberCredential = MemberCredential.builder()
			._id(memberUuid)
			.username(username)
			.password(encodedPassword)
			.role(guestRole)
			.loginTime(LocalDateTime.now())
			.build();

		memberCredentialRepository.save(newMemberCredential);

		// System.out.println(memberUuid+ " guestuuid");

		// 채널리스트 생성
		channelService.createChannelList(memberUuid);

		// 프로필 생성
		GuestProfile memberProfile = GuestProfile.builder()
				._id(newMemberCredential.get_id())
				.nickname(GuestUtil.makeNickname())
				.profilePalette(GuestUtil.chooseColorPalette())
				.build();

		// 프로필 저장
		memberProfileRepository.save(memberProfile);

		// // 게스트 access
		// String accessToken = jwtTokenProvider.createToken("access",username,guestRole, jwtSettings.getAccessExpire());
		// String refreshToken;
		// long cookieRefresh=jwtSettings.getGuestExpire();
		//
		// // 게스트 refresh 주기
		// refreshToken = jwtTokenProvider.createToken("refresh",username,guestRole, cookieRefresh);
		//
		// // redis에 refresh token 저장
		// redisService.storeRefreshToken(username, refreshToken, cookieRefresh);
		
		// 엑세스 리프레시 닉네임 프사
		// return List.of(accessToken, refreshToken, nickname, profilePalette);
		return GuestSignUpDto.builder()
			.username(username)
			.password(password)
			.build();
	}

	// id가 db에 있는지 체크. 있으면 false 없으면 true
	public boolean idCheck(String username) {

		return !memberCredentialRepository.existsByUsername(username);
	}

	// 파일 업로드
	public String uploadFile(MultipartFile file) throws IOException {
		//String fileName = file.getOriginalFilename();
		String fileName = String.valueOf(UUID.randomUUID());
		String fileUrl = baseUrl + fileName;

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		metadata.setContentLength(file.getSize());

		PutObjectRequest putObjectRequest = new PutObjectRequest(
			bucket, fileName, file.getInputStream(), metadata
		).withCannedAcl(CannedAccessControlList.PublicRead);

		amazonS3Client.putObject(putObjectRequest);
		return fileUrl;
	}


	public AccountProfileDto getProfileInfo()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		UUID uuid = userDetails.get_id();

		Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findById(uuid);
		if (memberProfileOptional.isEmpty()) {
			throw new CustomException(ErrorCode.NOTFOUND_USER);
		}

		if (!(memberProfileOptional.get() instanceof AccountProfile accountProfile))
		{
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
		return AccountProfileDto.builder()
				.myUuid(uuid)
				.nickname(accountProfile.getNickname())
				.username(userDetails.getUsername())
				.profileLink(accountProfile.getProfileLink())
				.role(accountProfile.getRole())
				.build();
	}
//	// b2c 회원정보 가져오기
//	public B2CProfileDto getB2CInfo() {
//
//		// 요청을 한 사용자의 uuid 구하기
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
//		UUID uuid = userDetails.get_id();
//
//		// 사용자의 profile 가져오기
//		Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findById(uuid);
//		if (memberProfileOptional.isEmpty()) {
//			throw new CustomException(ErrorCode.NOTFOUND_USER);
//		}
//		MemberProfile memberProfile = memberProfileOptional.get();
//
//
//		// 타입 체크
//		if (memberProfile instanceof B2CProfile b2cProfile) {
//			return B2CProfileDto.builder()
//				.myUuid(uuid)
//				.nickname(b2cProfile.getNickname())
//				.profile(b2cProfile.getProfileLink())
//				.build();
//		} else {
//			throw new CustomException(ErrorCode.INVALID_PARAMETER);
//		}
//	}
//
	//프로필 이미지 변경
	public void changeImage(UpdateImageDto b2CUpdateImageDto) {

		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		UUID uuid = userDetails.get_id();

		// 사용자의 profile 가져오기
		Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findById(uuid);
		if (memberProfileOptional.isEmpty()) {
			throw new CustomException(ErrorCode.NOTFOUND_USER);
		}
		MemberProfile memberProfile = memberProfileOptional.get();

		// 타입 체크
		if (memberProfile instanceof AccountProfile accountProfile) {
			String newProfileLink;

			// 새로 업로드 하고 링크 바꿔주기
			try {
				newProfileLink = uploadFile(b2CUpdateImageDto.getNewProfileImage());
			} catch (IOException e) {
				throw new CustomException(ErrorCode.INVALID_PARAMETER_IMAGE);
			}
			(accountProfile).setProfileLink(newProfileLink);
		} else {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		// 수정사항 저장
		memberProfileRepository.save(memberProfile);
	}
//
//	// b2c 닉네임 변경
//	public void changeNickname(B2CUpdateNicknameDto b2CUpdateNicknameDto) {
//
//		// 요청을 한 사용자의 uuid 구하기
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
//		UUID uuid = userDetails.get_id();
//
//		// 사용자의 profile 가져오기
//		Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findById(uuid);
//		if (memberProfileOptional.isEmpty()) {
//			throw new CustomException(ErrorCode.NOTFOUND_USER);
//		}
//		MemberProfile memberProfile = memberProfileOptional.get();
//
//		// 타입 체크
//		if (memberProfile instanceof B2CProfile b2cProfile) {
//			String newNickname = b2CUpdateNicknameDto.getNewNickname();
//			((B2CProfile)memberProfile).setNickname(newNickname);
//		} else {
//			throw new CustomException(ErrorCode.INVALID_PARAMETER);
//		}
//
//		// 수정사항 저장
//		memberProfileRepository.save(memberProfile);
//	}
//
//	// b2b 회원정보 가져오기
//	public B2BProfileDto getB2BInfo() {
//
//		// 요청을 한 사용자의 uuid 구하기
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
//		UUID uuid = userDetails.get_id();
//
//		// 사용자의 profile 가져오기
//		Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findById(uuid);
//		if (memberProfileOptional.isEmpty()) {
//			throw new CustomException(ErrorCode.NOTFOUND_USER);
//		}
//		MemberProfile memberProfile = memberProfileOptional.get();
//
//		// 타입 체크
//		if (memberProfile instanceof B2BProfile b2bProfile) {
//			return B2BProfileDto.builder()
//				.myUuid(uuid)
//				.companyName(b2bProfile.getCompanyName())
//				.companyEmail(b2bProfile.getCompanyEmail())
//				.build();
//		} else {
//			throw new CustomException(ErrorCode.INVALID_PARAMETER);
//		}
//	}

//	// b2b 정보 수정
//	public void changeB2BInfo(B2BUpdateDto b2bUpdateDto) {
//
//		// 요청을 한 사용자의 uuid 구하기
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
//		UUID uuid = userDetails.get_id();
//
//		// 사용자의 profile 가져오기
//		Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findById(uuid);
//		if (memberProfileOptional.isEmpty()) {
//			throw new CustomException(ErrorCode.NOTFOUND_USER);
//		}
//		MemberProfile memberProfile = memberProfileOptional.get();
//
//		// 타입 체크
//		if (memberProfile instanceof B2BProfile b2bProfile) {
//			String newCompanyName = b2bUpdateDto.getNewCompanyName();
//			String newCompanyEmail = b2bUpdateDto.getNewCompanyEmail();
//			((B2BProfile)memberProfile).setCompanyName(newCompanyName);
//			((B2BProfile)memberProfile).setCompanyEmail(newCompanyEmail);
//		} else {
//			throw new CustomException(ErrorCode.INVALID_PARAMETER);
//		}
//
//		// 수정사항 저장
//		memberProfileRepository.save(memberProfile);
//	}

		// b2b 정보 수정
	public void changeNickname(UpdateNicknameDto updateNicknameDto) {

		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		UUID uuid = userDetails.get_id();

		// 사용자의 profile 가져오기
		Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findById(uuid);
		if (memberProfileOptional.isEmpty()) {
			throw new CustomException(ErrorCode.NOTFOUND_USER);
		}
		MemberProfile memberProfile = memberProfileOptional.get();

		// 타입 체크
		if (memberProfile instanceof AccountProfile accountProfile) {
			accountProfile.setNickname(updateNicknameDto.getNewNickname());
		} else {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		// 수정사항 저장
		memberProfileRepository.save(memberProfile);
	}

	// 다른 사람의 정보 가져오기
	public MemberProfile getMemberInfo(UUIDDto uuidDto){

		UUID userUuid = uuidDto.getMemberUuid();
		// 사용자의 profile 가져오기
		Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findById(userUuid);
		if (memberProfileOptional.isEmpty()) {
			throw new CustomException(ErrorCode.NOTFOUND_USER);
		}

		return memberProfileOptional.get();
	}

	public String getMemberRole(UUIDDto uuidDto){
		UUID userUuid = uuidDto.getMemberUuid();
		// 사용자의 profile 가져오기
		Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findById(userUuid);
		if (memberProfileOptional.isEmpty()) {
			throw new CustomException(ErrorCode.NOTFOUND_USER);
		}

		if (memberProfileOptional.get() instanceof AccountProfile accountProfile)
		{
			return accountProfile.getRole();
		}
		else
		{
			return "guest";
		}
	}
	
	// guest 정보 가져오기
	public GuestProfileDto getGuestInfo() {

		// 요청을 한 사용자의 uuid 구하기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		UUID uuid = userDetails.get_id();

		// 사용자의 profile 가져오기
		Optional<MemberProfile> memberProfileOptional = memberProfileRepository.findById(uuid);
		if (memberProfileOptional.isEmpty()) {
			throw new CustomException(ErrorCode.NOTFOUND_USER);
		}
		MemberProfile memberProfile = memberProfileOptional.get();

		// 타입 체크
		if (memberProfile instanceof GuestProfile guestProfile) {
			return GuestProfileDto.builder()
				.myUuid(uuid)
				.nickname(guestProfile.getNickname())
				.profileImageColor(guestProfile.getProfilePalette())
				.build();
		} else {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
	}

	// guest가 링크 클릭했을 때 guestprofile이랑 chatroominfo 줘야함
	public LinkDto guestEnter(){

		// guest username, password 생성
		String guestUsername = generateUsername();
		String guestPassword = "guestPwd";

		// username 유효성 검사
		if (guestUsername.length() < 5 || guestUsername.length() > 20 || !Pattern.matches("^[A-Za-z0-9\\-_]+$", guestUsername)) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_ID);
		}

		// 비밀번호 암호화
		String encodedPassword = bCryptPasswordEncoder.encode(guestPassword);

		// 사용자 uuid 생성
		UUID memberUuid = UUID.randomUUID();

		// role=guest
		String guestRole = "guest";

		MemberCredential newMemberCredential = MemberCredential.builder()
			._id(memberUuid)
			.username(guestUsername)
			.password(encodedPassword)
			.role(guestRole)
			.loginTime(LocalDateTime.now())
			.build();

		memberCredentialRepository.save(newMemberCredential);

		// System.out.println(memberUuid+ " guestuuid");

		// 채널리스트 생성
		channelService.createChannelList(memberUuid);

		// 프로필 생성
		GuestProfile memberProfile = GuestProfile.builder()
				._id(newMemberCredential.get_id())
				.nickname(GuestUtil.makeNickname())
				.profilePalette(GuestUtil.chooseColorPalette())
				.build();

		// 프로필 저장
		memberProfileRepository.save(memberProfile);



		// // 게스트 access
		// String accessToken = jwtTokenProvider.createToken("access",username,guestRole, jwtSettings.getAccessExpire());
		// String refreshToken;
		// long cookieRefresh=jwtSettings.getGuestExpire();
		//
		// // 게스트 refresh 주기
		// refreshToken = jwtTokenProvider.createToken("refresh",username,guestRole, cookieRefresh);
		//
		// // redis에 refresh token 저장
		// redisService.storeRefreshToken(username, refreshToken, cookieRefresh);

		// 엑세스 리프레시 닉네임 프사
		//return List.of(accessToken, refreshToken, nickname, profilePalette);

		return null;
	}


	public void tempGuestSignUp(){
		// guest username, password 생성
		String guestUsername = generateUsername();
		String guestPassword = "guestPwd";

		// username 유효성 검사
		if (guestUsername.length() < 5 || guestUsername.length() > 20 || !Pattern.matches("^[A-Za-z0-9\\-_]+$", guestUsername)) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_ID);
		}

		// 비밀번호 암호화
		String encodedPassword = bCryptPasswordEncoder.encode(guestPassword);

		// 사용자 uuid 생성
		UUID memberUuid = UUID.randomUUID();

		// role=guest
		String guestRole = "guest";

		MemberCredential newMemberCredential = MemberCredential.builder()
			._id(memberUuid)
			.username(guestUsername)
			.password(encodedPassword)
			.role(guestRole)
			.loginTime(LocalDateTime.now())
			.build();

		memberCredentialRepository.save(newMemberCredential);

		// 채널리스트 생성
		channelService.createChannelList(memberUuid);

		// 프로필 생성
		GuestProfile memberProfile = GuestProfile.builder()
				._id(newMemberCredential.get_id())
				.nickname(GuestUtil.makeNickname())
				.profilePalette(GuestUtil.chooseColorPalette())
				.build();
		// 프로필 저장
		memberProfileRepository.save(memberProfile);
	}

	// 이메일 유효성 검사 함수
	private boolean invalidEmail(String email) {
		// 정규식: 이메일 형식 검사 (일반적인 RFC 5322 형식 참고)
		String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

		// 정규식 검사
		if (!Pattern.matches(emailRegex, email)) {
			return true;
		}

		// '@' 문자가 반드시 하나여야 함
		return email.chars().filter(ch -> ch == '@').count() != 1;
	}



}
