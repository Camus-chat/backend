package com.camus.backend.member.controller;

import com.camus.backend.global.Exception.CustomException;
import com.camus.backend.global.util.SuccessCode;
import com.camus.backend.member.domain.dto.*;
import com.camus.backend.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    ResponseEntity<?> SignUp(@RequestBody MemberCredentialDto memberCredentialDto){
        System.out.println("check");
        memberService.memberSignUp(memberCredentialDto);
        return ResponseEntity.ok(SuccessCode.SIGNUP);
    }

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

    @PostMapping("/etc/check")
    ResponseEntity<?> idCheck(@RequestBody SignUpDto signUpDto){
        return ResponseEntity.ok(memberService.idCheck(signUpDto.getUsername()));
    }

    @PostMapping("/etc/info")
    public ResponseEntity<?> getMemberInfo(@RequestBody UUIDDto uuidDto) {
        return ResponseEntity.ok(memberService.getMemberInfo(uuidDto));
    }
}
