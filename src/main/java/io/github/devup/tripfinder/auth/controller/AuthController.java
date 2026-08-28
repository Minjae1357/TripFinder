package io.github.devup.tripfinder.auth.controller;

import io.github.devup.tripfinder.auth.dto.request.EmailRequest;
import io.github.devup.tripfinder.auth.dto.request.EmailVerifyRequest;
import io.github.devup.tripfinder.auth.dto.request.LoginRequest;
import io.github.devup.tripfinder.auth.dto.request.SignupRequest;
import io.github.devup.tripfinder.auth.dto.response.TokenResponse;
import io.github.devup.tripfinder.auth.dto.response.UserInfoResponse;
import io.github.devup.tripfinder.auth.entity.Users;
import io.github.devup.tripfinder.auth.service.AuthService;
import io.github.devup.tripfinder.auth.service.EmailService;
import io.github.devup.tripfinder.config.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


//@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtProvider jwtProvider;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
        TokenResponse tokens = authService.login(loginRequest);

        ResponseCookie cookie = jwtProvider.createRefreshTokenCookie(tokens.getRefreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(Map.of("accessToken", tokens.getAccessToken()));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication, HttpServletResponse response){
        if(authentication == null){
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        Long userId = (Long) authentication.getPrincipal();
        ResponseCookie cookie = authService.logout(userId);
        response.addHeader(HttpHeaders.SET_COOKIE,cookie.toString());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email-check")
    public boolean checkEmailDuplicate(@RequestBody EmailRequest emailRequest){
        return authService.isEmailDuplicate(emailRequest.getEmail(),emailRequest.getProvider());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication  authentication){
        if(authentication == null){
            throw new IllegalArgumentException("로그인이 필요합니다");
        }
        Long userId = (Long) authentication.getPrincipal();
        UserInfoResponse me = authService.getMe(userId);
        return ResponseEntity.ok(me);
    }
}
