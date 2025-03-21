package com.camus.backend.member.controller;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.util.SuccessCode;
import com.camus.backend.member.domain.dto.*;
import com.camus.backend.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    @Operation(
            summary = "멤버 회원가입",
            description = "b2c/b2b 회원가입을 위한 API"+
                    "POST member/login { username:\"string\", password:\"string\" } 으로 로그인할 수 있습니다."
    )
    @ApiResponse(responseCode = "200", description = "회원가입 성공",
            content = @Content(mediaType = "text/plain",
                    schema = @Schema(type = "string"),
                    examples = @ExampleObject(value = "SIGNUP")
            )
    )
    @PostMapping("/signup")
    ResponseEntity<SuccessCode> SignUp(@RequestBody MemberCredentialDto memberCredentialDto){
        System.out.println("check");
        memberService.memberSignUp(memberCredentialDto);
        return ResponseEntity.ok(SuccessCode.SIGNUP);
    }

    @Operation(
            summary = "멤버 회원정보 조회",
            description = "b2c/b2b 멤버가 자신의 정보를 조회하는 API"
    )
    @ApiResponse(responseCode = "200", description = "회원정보 조회 성공",
            content = @Content(schema = @Schema(implementation = AccountProfileDto.class)))
    @GetMapping("/info")
    public ResponseEntity<?> getMemberInfo() {
        try {
            AccountProfileDto accountProfileDto = memberService.getProfileInfo();
            return ResponseEntity.ok(accountProfileDto);
        } catch (CustomException e) {
            // 커스텀 예외를 사용하여 에러코드를 기반으로 에러 응답 생성
            return ResponseEntity
                    .status(e.getHttpStatusCode())
                    .body(e.getErrorKey());
        }
    }

    @Operation(
            summary = "멤버 닉네임 변경",
            description = "b2c/b2b 멤버가 닉네임을 변경하는 API"
    )
    @ApiResponse(responseCode = "200", description = "회원정보 변경 성공",
            content = @Content(mediaType = "text/plain",
                    schema = @Schema(type = "string"),
                    examples = @ExampleObject(value = "NICKNAME_EDIT")
            )
    )
    @PatchMapping("/nickname")
    public ResponseEntity<?> changeNickname(@RequestBody UpdateNicknameDto updateNicknameDto) {
        try {
            memberService.changeNickname(updateNicknameDto);
            return ResponseEntity.ok(SuccessCode.NICKNAME_EDIT);
        } catch (CustomException e) {
            return ResponseEntity
                    .status(e.getHttpStatusCode())
                    .body(e.getErrorKey());
        }
    }


    @Operation(
            summary = "멤버 이미지 변경",
            description = "b2c/b2b 멤버가 프로필 이미지를 변경하는 API"
    )
    @ApiResponse(responseCode = "200", description = "프로필 변경 성공",
            content = @Content(mediaType = "text/plain",
                    schema = @Schema(type = "string"),
                    examples = @ExampleObject(value = "PROFILE_EDIT")
            )
    )
    @PatchMapping("/image")
    public ResponseEntity<?> changeProfileImage(@ModelAttribute UpdateImageDto updateImageDto) {
        try {
            memberService.changeImage(updateImageDto);
            return ResponseEntity.ok(SuccessCode.PROFILE_EDIT);
        } catch (CustomException e) {
            return ResponseEntity
                    .status(e.getHttpStatusCode())
                    .body(e.getErrorKey());
        }
    }
    @Operation(
            summary = "멤버 id 중복 체크",
            description = "회원 가입 이전 id가 중복되었는지 확인하는 API"
    )
    @PostMapping("/etc/check")
    ResponseEntity<Boolean> idCheck(@RequestBody SignUpDto signUpDto){
        return ResponseEntity.ok(memberService.idCheck(signUpDto.getUsername()));
    }

    @Operation(summary = "사용하지 않는 엔드포인트", description = "필요시 리팩토링 요청")
    @PostMapping("/etc/info")
    public ResponseEntity<?> getMemberInfo(@RequestBody UUIDDto uuidDto) {
        return ResponseEntity.ok(memberService.getMemberInfo(uuidDto));
    }
}
