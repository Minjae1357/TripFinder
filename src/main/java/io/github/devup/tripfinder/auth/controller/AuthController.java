package io.github.devup.tripfinder.auth.controller;

import io.github.devup.tripfinder.auth.dto.request.EmailRequest;
import io.github.devup.tripfinder.auth.dto.request.EmailVerifyRequest;
import io.github.devup.tripfinder.auth.dto.request.LoginRequest;
import io.github.devup.tripfinder.auth.dto.request.SignupRequest;
import io.github.devup.tripfinder.auth.dto.response.TokenResponse;
import io.github.devup.tripfinder.auth.entity.Users;
import io.github.devup.tripfinder.auth.service.AuthService;
import io.github.devup.tripfinder.auth.service.EmailService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    // 이메일 인증번호 발송 api 부분
    @PostMapping("/email-send")
    public void sendEmail(@RequestBody EmailRequest request){
        emailService.sendVerificationCode(request.getEmail());
    }
    // 이메일 인증번호 검증 api
    @PostMapping("/email-verify")
    public void verifyEmail(@RequestBody EmailVerifyRequest request){
        boolean isVerified = emailService.verifyCode(request.getEmail(),request.getCode());

        if(!isVerified){
            throw new IllegalArgumentException("인증버호가 일치하지 않거나 만료되었습니다.");
        }
    }

    @PostMapping("/signup")
    public void signup(@RequestBody SignupRequest signupRequest) {
       authService.signup(signupRequest);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }
}
