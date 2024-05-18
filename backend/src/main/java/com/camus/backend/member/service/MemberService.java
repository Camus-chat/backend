package com.camus.backend.member.service;

import static com.camus.backend.global.util.GuestUtil.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
import com.camus.backend.global.jwt.service.RedisService;
import com.camus.backend.global.jwt.util.JwtSettings;
import com.camus.backend.global.jwt.util.JwtTokenProvider;
import com.camus.backend.global.util.GuestUtil;
import com.camus.backend.manage.service.ChannelService;
import com.camus.backend.member.domain.document.MemberCredential;
import com.camus.backend.member.domain.document.MemberProfile.B2BProfile;
import com.camus.backend.member.domain.document.MemberProfile.B2CProfile;
import com.camus.backend.member.domain.document.MemberProfile.GuestProfile;
import com.camus.backend.member.domain.document.MemberProfile.MemberProfile;
import com.camus.backend.member.domain.dto.B2BMemberCredentialDto;
import com.camus.backend.member.domain.dto.B2BProfileDto;
import com.camus.backend.member.domain.dto.B2BUpdateDto;
import com.camus.backend.member.domain.dto.B2CMemberCredentialDto;
import com.camus.backend.member.domain.dto.B2CProfileDto;
import com.camus.backend.member.domain.dto.B2CUpdateImageDto;
import com.camus.backend.member.domain.dto.B2CUpdateNicknameDto;
import com.camus.backend.member.domain.dto.CustomUserDetails;
import com.camus.backend.member.domain.dto.GuestProfileDto;
import com.camus.backend.member.domain.dto.GuestSignUpDto;
import com.camus.backend.member.domain.dto.LinkDto;
import com.camus.backend.member.domain.dto.MemberCredentialDto;

import com.camus.backend.member.domain.dto.UUIDDto;
import com.camus.backend.member.domain.repository.MemberCredentialRepository;
import com.camus.backend.member.domain.repository.MemberProfileRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private final JwtSettings jwtSettings;
	private final JwtTokenProvider jwtTokenProvider;
	private final RedisService redisService;

	public MemberService(MemberCredentialRepository memberCredentialRepository,
		BCryptPasswordEncoder bCryptPasswordEncoder, MemberProfileRepository memberProfileRepository,
		AmazonS3Client amazonS3Client, ChannelService channelService, JwtSettings jwtSettings,
		JwtTokenProvider jwtTokenProvider, RedisService redisService) {
		this.memberCredentialRepository = memberCredentialRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.memberProfileRepository = memberProfileRepository;
		this.amazonS3Client = amazonS3Client;
		this.channelService = channelService;
		this.jwtSettings = jwtSettings;
		this.jwtTokenProvider = jwtTokenProvider;
		this.redisService = redisService;
	}




	// 회원가입(db에 넣기) 후 프론트에 아이디, 비번 보내기
	public void signUp(MemberCredentialDto memberCredentialDto, String role) {
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

		// password 유효성 검사
		if (password == null || password.trim().isEmpty()) {
			throw new CustomException(ErrorCode.MISSING_PARAMETER_PW);
		}

		// // 이미 사용되는 username이면 생성 못함
		// Boolean isExist=memberCredentialRepository.existsByUsername(username);
		// if(isExist){
		// 	return false;
		// }

		// 비밀번호 암호화
		String encodedPassword = bCryptPasswordEncoder.encode(password);

		// 사용자 uuid 생성
		UUID memberUuid = UUID.randomUUID();

		MemberCredential newMemberCredential = MemberCredential.builder()
			._id(memberUuid)
			.username(username)
			.password(encodedPassword)
			.role(role)
			.loginTime(LocalDateTime.now())
			.build();

		memberCredentialRepository.save(newMemberCredential);

		// 채널리스트 생성
		channelService.createChannelList(memberUuid);

		MemberProfile memberProfile;
		if ("b2b".equals(role)) {
			memberProfile = new B2BProfile();

			// 프로필 ID 설정
			memberProfile.set_id(newMemberCredential.get_id());

			String companyName = memberCredentialDto.getInput1();
			String companyEmail = (String)memberCredentialDto.getInput2();

			// companyName 유효성 검사
			if (companyName == null || companyName.trim().isEmpty()) {
				throw new CustomException(ErrorCode.MISSING_PARAMETER_CN);
			}
			// companyEmail 유효성 검사
			if (companyEmail == null || companyEmail.trim().isEmpty()) {
				throw new CustomException(ErrorCode.MISSING_PARAMETER_EM);
			}

			((B2BProfile)memberProfile).setCompanyName(companyName);
			((B2BProfile)memberProfile).setCompanyEmail(companyEmail);
		} else if ("b2c".equals(role)) {
			memberProfile = new B2CProfile();

			// 프로필 ID 설정
			memberProfile.set_id(newMemberCredential.get_id());

			String nickname = memberCredentialDto.getInput1();
			String profileLink = null;
			try {
				profileLink = uploadFile((MultipartFile)memberCredentialDto.getInput2());
			} catch (IOException e) {
				throw new CustomException(ErrorCode.INVALID_PARAMETER_IMAGE);
			}
			((B2CProfile)memberProfile).setNickname(nickname);
			((B2CProfile)memberProfile).setProfileLink(profileLink);
		} else {

			// // guest 로직
			// memberProfile = new GuestProfile();
			// 프로필 ID 설정
			// memberProfile.set_id(newMemberCredential.get_id());
			// String nickname = GuestUtil.makeNickname();
			// String profilePalette = GuestUtil.chooseColorPalette();
			// ((GuestProfile)memberProfile).setNickname(nickname);
			// ((GuestProfile)memberProfile).setProfilePalette(profilePalette);
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		// // 프로필 ID 설정
		// memberProfile.set_id(newMemberCredential.get_id());

		// 프로필 저장
		memberProfileRepository.save(memberProfile);

		// List<String> credentials = new ArrayList<>();
		// credentials.add(username);
		// credentials.add(password);

		// 시간은 한국 시간으로 정상적으로 찍히네
		// System.out.println(memberCredentialRepository.findByUsername(username).getLoginTime());

		// 저장 후 username과 암호화된 password를 리스트로 반환
		return;
	}

	public void b2bSignUp(B2BMemberCredentialDto b2bMemberCredentialDto, String role) {
		String username = b2bMemberCredentialDto.getUsername();
		String password = b2bMemberCredentialDto.getPassword();

		// System.out.println(username+" dd");
		// System.out.println(password+" ss");

		// username 받았는지 검사
		if (username == null || username.trim().isEmpty()) {
			throw new CustomException(ErrorCode.MISSING_PARAMETER_ID);
		}

		// username 유효성 검사
		if (username.length() < 5 || username.length() > 20 || !Pattern.matches("^[A-Za-z0-9\\-_]+$", username)) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_ID);
		}

		// password 유효성 검사
		if (password == null || password.trim().isEmpty()) {
			throw new CustomException(ErrorCode.MISSING_PARAMETER_PW);
		}

		// // 이미 사용되는 username이면 생성 못함
		// Boolean isExist=memberCredentialRepository.existsByUsername(username);
		// if(isExist){
		// 	return false;
		// }

		// 비밀번호 암호화
		String encodedPassword = bCryptPasswordEncoder.encode(password);

		// 사용자 uuid 생성
		UUID memberUuid = UUID.randomUUID();

		MemberCredential newMemberCredential = MemberCredential.builder()
			._id(memberUuid)
			.username(username)
			.password(encodedPassword)
			.role(role)
			.loginTime(LocalDateTime.now())
			.build();

		memberCredentialRepository.save(newMemberCredential);

		// System.out.println(memberUuid+ " b2buuid");

		// 채널리스트 생성
		channelService.createChannelList(memberUuid);

		B2BProfile memberProfile = new B2BProfile();
		if ("b2b".equals(role)) {

			// 프로필 ID 설정
			memberProfile.set_id(newMemberCredential.get_id());

			String companyName = b2bMemberCredentialDto.getCompanyName();
			String companyEmail = b2bMemberCredentialDto.getCompanyEmail();

			// companyName 유효성 검사
			if (companyName == null || companyName.trim().isEmpty()) {
				throw new CustomException(ErrorCode.MISSING_PARAMETER_CN);
			}
			// companyEmail 유효성 검사
			if (companyEmail == null || companyEmail.trim().isEmpty()) {
				throw new CustomException(ErrorCode.MISSING_PARAMETER_EM);
			}

			memberProfile.setCompanyName(companyName);
			memberProfile.setCompanyEmail(companyEmail);
		} else {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		// // 프로필 ID 설정
		// memberProfile.set_id(newMemberCredential.get_id());

		// 프로필 저장
		memberProfileRepository.save(memberProfile);
	}

	public void b2cSignUp(B2CMemberCredentialDto b2cMemberCredentialDto, String role) {
		String username = b2cMemberCredentialDto.getUsername();
		String password = b2cMemberCredentialDto.getPassword();

		// System.out.println(username+" dd");
		// System.out.println(password+" ss");

		// username 받았는지 검사
		if (username == null || username.trim().isEmpty()) {
			throw new CustomException(ErrorCode.MISSING_PARAMETER_ID);
		}

		// username 유효성 검사
		if (username.length() < 5 || username.length() > 20 || !Pattern.matches("^[A-Za-z0-9\\-_]+$", username)) {
			throw new CustomException(ErrorCode.INVALID_PARAMETER_ID);
		}

		// password 유효성 검사
		if (password == null || password.trim().isEmpty()) {
			throw new CustomException(ErrorCode.MISSING_PARAMETER_PW);
		}

		// // 이미 사용되는 username이면 생성 못함
		// Boolean isExist=memberCredentialRepository.existsByUsername(username);
		// if(isExist){
		// 	return false;
		// }

		// 비밀번호 암호화
		String encodedPassword = bCryptPasswordEncoder.encode(password);

		// 사용자 uuid 생성
		UUID memberUuid = UUID.randomUUID();

		MemberCredential newMemberCredential = MemberCredential.builder()
			._id(memberUuid)
			.username(username)
			.password(encodedPassword)
			.role(role)
			.loginTime(LocalDateTime.now())
			.build();

		memberCredentialRepository.save(newMemberCredential);

		// System.out.println(memberUuid+ " b2cuuid");

		// 채널리스트 생성
		channelService.createChannelList(memberUuid);

		B2CProfile memberProfile = new B2CProfile();
		if ("b2c".equals(role)) {

			// 프로필 ID 설정
			memberProfile.set_id(newMemberCredential.get_id());

			String nickname = b2cMemberCredentialDto.getNickname();
			// nickname 유효성 검사
			if (nickname == null || nickname.trim().isEmpty()) {
				throw new CustomException(ErrorCode.MISSING_PARAMETER_CN);
			}

			String profileLink = null;
			try {
				profileLink = uploadFile(b2cMemberCredentialDto.getProfileImage());
			} catch (IOException e) {
				throw new CustomException(ErrorCode.INVALID_PARAMETER_IMAGE);
			}

			memberProfile.setNickname(nickname);
			memberProfile.setProfileLink(profileLink);
		} else {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

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
		GuestProfile memberProfile = new GuestProfile();
		// 프로필 ID 설정
		memberProfile.set_id(newMemberCredential.get_id());
		String nickname = GuestUtil.makeNickname();
		String profilePalette = GuestUtil.chooseColorPalette();
		memberProfile.setNickname(nickname);
		memberProfile.setProfilePalette(profilePalette);

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

	// b2c 회원정보 가져오기
	public B2CProfileDto getB2CInfo() {

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
		if (memberProfile instanceof B2CProfile b2cProfile) {
			return B2CProfileDto.builder()
				.nickname(b2cProfile.getNickname())
				.profileLink(b2cProfile.getProfileLink())
				.build();
		} else {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
	}

	// b2c 프로필 이미지 변경
	public void changeImage(B2CUpdateImageDto b2CUpdateImageDto) {

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
		if (memberProfile instanceof B2CProfile b2cProfile) {
			String newProfileLink;

			// 새로 업로드 하고 링크 바꿔주기
			try {
				newProfileLink = uploadFile(b2CUpdateImageDto.getNewProfileImage());
			} catch (IOException e) {
				throw new CustomException(ErrorCode.INVALID_PARAMETER_IMAGE);
			}
			((B2CProfile)memberProfile).setProfileLink(newProfileLink);
		} else {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		// 수정사항 저장
		memberProfileRepository.save(memberProfile);
	}

	// b2c 닉네임 변경
	public void changeNickname(B2CUpdateNicknameDto b2CUpdateNicknameDto) {

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
		if (memberProfile instanceof B2CProfile b2cProfile) {
			String newNickname = b2CUpdateNicknameDto.getNewNickname();
			((B2CProfile)memberProfile).setNickname(newNickname);
		} else {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}

		// 수정사항 저장
		memberProfileRepository.save(memberProfile);
	}

	// b2b 회원정보 가져오기
	public B2BProfileDto getB2BInfo() {

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
		if (memberProfile instanceof B2BProfile b2bProfile) {
			return B2BProfileDto.builder()
				.companyName(b2bProfile.getCompanyName())
				.companyEmail(b2bProfile.getCompanyEmail())
				.build();
		} else {
			throw new CustomException(ErrorCode.INVALID_PARAMETER);
		}
	}

	// b2b 정보 수정
	public void changeB2BInfo(B2BUpdateDto b2bUpdateDto) {

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
		if (memberProfile instanceof B2BProfile b2bProfile) {
			String newCompanyName = b2bUpdateDto.getNewCompanyName();
			String newCompanyEmail = b2bUpdateDto.getNewCompanyEmail();
			((B2BProfile)memberProfile).setCompanyName(newCompanyName);
			((B2BProfile)memberProfile).setCompanyEmail(newCompanyEmail);
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
		GuestProfile memberProfile = new GuestProfile();
		// 프로필 ID 설정
		memberProfile.set_id(memberUuid);
		String nickname = GuestUtil.makeNickname();
		String profilePalette = GuestUtil.chooseColorPalette();
		memberProfile.setNickname(nickname);
		memberProfile.setProfilePalette(profilePalette);

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
		GuestProfile memberProfile = new GuestProfile();
		// 프로필 ID 설정
		memberProfile.set_id(memberUuid);
		String nickname = GuestUtil.makeNickname();
		String profilePalette = GuestUtil.chooseColorPalette();
		memberProfile.setNickname(nickname);
		memberProfile.setProfilePalette(profilePalette);

		// 프로필 저장
		memberProfileRepository.save(memberProfile);
	}




}
