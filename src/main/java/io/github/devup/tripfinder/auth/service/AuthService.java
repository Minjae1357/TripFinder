package io.github.devup.tripfinder.auth.service;

import io.github.devup.tripfinder.auth.dto.oauth.OAuth2UserInfo;
import io.github.devup.tripfinder.auth.dto.request.LoginRequest;
import io.github.devup.tripfinder.auth.dto.request.SignupRequest;
import io.github.devup.tripfinder.auth.dto.response.TokenResponse;
import io.github.devup.tripfinder.auth.entity.Users;
import io.github.devup.tripfinder.auth.repository.UsersRepository;
import io.github.devup.tripfinder.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UsersRepository usersRepository;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;

    @Transactional
    public Users signup(SignupRequest signupRequest) {

        boolean isSocial = signupRequest.getSocialUid() != null;
        System.out.println(isSocial);
        if(!isSocial) {
            if(!emailService.isEmailVerified(signupRequest.getLoginEmail())){
                throw new IllegalArgumentException("이메일 인증이 필요합니다.");
            }
        }
        String encodedPassword = null;
        BCryptPasswordEncoder passwordEncoder =new BCryptPasswordEncoder();
        encodedPassword = passwordEncoder.encode(signupRequest.getLoginPassword());




        Users user = Users.builder()
                .loginEmail(signupRequest.getLoginEmail())
                .loginPassword(encodedPassword)
                .provider(isSocial ? signupRequest.getProvider() : "local")
                .socialUid(isSocial ? signupRequest.getSocialUid() : null)
                .nickname(signupRequest.getNickname())
                .location(signupRequest.getLocation())
                .ageGroup(signupRequest.getAgeGroup())
                .gender(signupRequest.getGender())
                .build();
        return usersRepository.save(user);
    }

    @Transactional
    public Users socialLoginOrSignUp(OAuth2UserInfo userInfo){
        return usersRepository.findByProviderAndSocialUid(userInfo.getProvider(),userInfo.getProviderId())
                .orElseGet(() ->{ //Optional 클래스에서 "값이 비어있을(null일) 때만 대체 값을 생성하여 반환
                                  //Optional : 값이 존재할 수도 있고, null일 수도 있는 객체"를 감싸는 Wrapper(래퍼) 클래스
                    Users newUser = Users.builder()
                            .provider(userInfo.getProvider())
                            .socialUid(userInfo.getProviderId())
                            .loginEmail(userInfo.getEmail())
                            .nickname(userInfo.getNickname() != null ? userInfo.getNickname() : "user_" + userInfo.getProviderId())
                            .profileUrl(userInfo.getProfileUrl())
                            .build();
                    return usersRepository.save(newUser);
                });
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest loginRequest){
        // 유저를 확인하고
        Users user = usersRepository.findByLoginEmail(loginRequest.getLoginEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지않은 이메일 입니다."));
        // 비밀버놓 검증
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(!passwordEncoder.matches(loginRequest.getLoginPassword(),user.getLoginPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // Jwtprovider에서 토큰 생성만 요청
        String accessToken = jwtProvider.createAccessToken(user.getId(),user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        return new TokenResponse(accessToken,refreshToken);
    }

    public boolean isEmailDuplicate(String email) {
        return usersRepository.existsByLoginEmail(email);
    }
}
