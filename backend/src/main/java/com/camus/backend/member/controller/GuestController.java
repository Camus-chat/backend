package com.camus.backend.member.controller;

import static com.camus.backend.global.util.GuestUtil.*;

import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.Exception.ErrorCode;
import com.camus.backend.global.util.SuccessCode;
import com.camus.backend.manage.domain.dto.RoomIdDto;
import com.camus.backend.manage.service.RoomService;
import com.camus.backend.manage.util.ChannelStatus;
import com.camus.backend.manage.util.RoomEntryManager;
import com.camus.backend.member.domain.dto.B2BProfileDto;
import com.camus.backend.member.domain.dto.CustomUserDetails;
import com.camus.backend.member.domain.dto.GuestProfileDto;
import com.camus.backend.member.domain.dto.MemberCredentialDto;
import com.camus.backend.member.domain.dto.SignUpDto;
import com.camus.backend.member.service.MemberService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/guest")
public class GuestController {

	private final MemberService memberService;
	private final RoomService roomService;

	public GuestController(MemberService memberService, RoomService roomService) {
		this.memberService = memberService;
		this.roomService = roomService;
	}

	@GetMapping("/signup")
	public ResponseEntity<?> guestSignUp(){
		// String role=memberCredentialDto.getRole();
		// boolean signUpSuccess = memberService.signUp(memberCredentialDto,role);

		// memberCredentialDto 새로 생성
		MemberCredentialDto memberCredentialDto = MemberCredentialDto.builder()
			.username(generateUsername())
			.password("guestPwd")
			.build();

		// memberService.guestSignUp(memberCredentialDto);

		// memberService.guestSignUp(memberCredentialDto);

		// memberService.signUp(memberCredentialDto,"guest");
		// List<String> credentials = memberService.signUp(memberCredentialDto,"guest");
		// if (credentials.isEmpty()) {
		// 	return ResponseEntity.badRequest().body("게스트 생성 에러입니다.");
		// }
		return ResponseEntity.ok(memberService.guestSignUp(memberCredentialDto));
	}

	@GetMapping("/info")
	public ResponseEntity<?> getGuestInfo() {
		try {
			GuestProfileDto guestProfileDto = memberService.getGuestInfo();
			return ResponseEntity.ok(guestProfileDto);
		} catch (CustomException e) {
			// 커스텀 예외를 사용하여 에러코드를 기반으로 에러 응답 생성
			return ResponseEntity
				.status(e.getHttpStatusCode())
				.body(e.getErrorKey());
		}
	}

	// @GetMapping("/enter")
	// public ResponseEntity<?> guestEnterRoom(@RequestBody UUID channelLink){
	//
	// 	// // 요청을 한 사용자의 uuid 구하기
	// 	// Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	// 	// CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	// 	// UUID userUuid = userDetails.get_id();
	//
	//
	// 	RoomEntryManager roomEntryManager;
	// 	// TODO : 기존에 그 채널에 들어가 있는가? 체크 => 진입
	// 	roomEntryManager = roomService.isChannelMember(userUuid, channelLink);
	//
	// 	if (roomEntryManager.isCheck()) {
	// 		return ResponseEntity.ok(RoomIdDto.builder().roomId(
	// 			roomEntryManager.getRoomId()
	// 		).build());
	// 	}
	//
	// 	ChannelStatus channelStatus = roomService.channelStatus(channelLink);
	//
	// 	// TODO : 채널 링크가 유효한가? 체크 => 진입
	// 	if (!channelStatus.isValid()) {
	// 		throw new CustomException(ErrorCode.NOTFOUND_CHANNEL);
	// 	}
	//
	// 	// TODO : 개인 : 새로운 ROOM 생성 => 진입
	// 	if (channelStatus.getType().equals("private")) {
	//
	// 		// 입장 성공
	// 		return
	// 			ResponseEntity.ok(RoomIdDto.builder().roomId(
	// 				roomService.createPrivateRoomByGuestId(
	// 					channelStatus.getKey(),
	// 					channelStatus.getOwnerId(), userUuid)
	// 			).build());
	// 	}
	//
	// 	// TODO : 단체 : 기존 ROOM에 입장
	// 	return
	// 		ResponseEntity.ok(RoomIdDto.builder().roomId(
	// 			roomService.joinGroupRoom(channelStatus.getKey(), userUuid)
	// 		).build());
	//
	// }
	//
	//
	// @PostMapping("/guest/enter")
	// public ResponseEntity<RoomIdDto> enterRoom(
	// 	// TODO : 사용자 인증 정보
	// 	@RequestBody UUID channelLink
	// ) {
	// 	// 요청을 한 사용자의 uuid 구하기
	// 	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	// 	CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	// 	UUID userUuid = userDetails.get_id();
	//
	// 	// CHECK : 여기서 이미 사용자 인증이 되었다고 가정
	// 	//UUID tempMemberId = ManageConstants.tempMemUuid;
	//
	// 	RoomEntryManager roomEntryManager;
	// 	// TODO : 기존에 그 채널에 들어가 있는가? 체크 => 진입
	// 	roomEntryManager = roomService.isChannelMember(userUuid, channelLink);
	//
	// 	if (roomEntryManager.isCheck()) {
	// 		return ResponseEntity.ok(RoomIdDto.builder().roomId(
	// 			roomEntryManager.getRoomId()
	// 		).build());
	// 	}
	//
	// 	ChannelStatus channelStatus = roomService.channelStatus(channelLink);
	//
	// 	// TODO : 채널 링크가 유효한가? 체크 => 진입
	// 	if (!channelStatus.isValid()) {
	// 		throw new CustomException(ErrorCode.NOTFOUND_CHANNEL);
	// 	}
	//
	// 	// TODO : 개인 : 새로운 ROOM 생성 => 진입
	// 	if (channelStatus.getType().equals("private")) {
	//
	// 		// 입장 성공
	// 		return
	// 			ResponseEntity.ok(RoomIdDto.builder().roomId(
	// 				roomService.createPrivateRoomByGuestId(
	// 					channelStatus.getKey(),
	// 					channelStatus.getOwnerId(), userUuid)
	// 			).build());
	// 	}
	//
	// 	// TODO : 단체 : 기존 ROOM에 입장
	// 	return
	// 		ResponseEntity.ok(RoomIdDto.builder().roomId(
	// 			roomService.joinGroupRoom(channelStatus.getKey(), userUuid)
	// 		).build());
	// }


}
