package io.github.devup.tripfinder.auth.service;

import io.github.devup.tripfinder.auth.dto.oauth.OAuth2UserInfo;
import io.github.devup.tripfinder.auth.dto.request.LoginRequest;
import io.github.devup.tripfinder.auth.dto.request.SignupRequest;
import io.github.devup.tripfinder.auth.dto.response.TokenResponse;
import io.github.devup.tripfinder.auth.dto.response.UserInfoResponse;
import io.github.devup.tripfinder.auth.entity.Users;
import io.github.devup.tripfinder.auth.repository.UsersRepository;
import io.github.devup.tripfinder.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
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
        BCryptPasswordEncoder passwordEncoder =new BCryptPasswordEncoder();
        boolean isSocial = signupRequest.getSocialUid() != null;
        String  encodedPassword = isSocial ? null : passwordEncoder.encode(signupRequest.getLoginPassword());
        System.out.println(isSocial);
        if(!isSocial) {
            if(!emailService.isEmailVerified(signupRequest.getLoginEmail())){
                throw new IllegalArgumentException("이메일 인증이 필요합니다.");
            }
        }
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
    public TokenResponse login(LoginRequest loginRequest){
        // 유저를 확인하고
        Users user = usersRepository.findByLoginEmailAndProvider(loginRequest.getLoginEmail(),"local")
                .orElseThrow(() -> new IllegalArgumentException("가입되지않은 이메일 입니다."));
        // 비밀버놓 검증
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if(!passwordEncoder.matches(loginRequest.getLoginPassword(),user.getLoginPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // Jwtprovider에서 토큰 생성만 요청
        String accessToken = jwtProvider.createAccessToken(user.getId(),user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        // 재발급 시 대조하기 위해 DB에도 저장해둠 (탈취된 옛날 refreshToken 재사용 방지)
        user.updateRefreshToken(refreshToken);

        return new TokenResponse(accessToken,refreshToken);
    }

    @Transactional
    public ResponseCookie logout(Long userId){
        usersRepository.findById(userId).ifPresent(user -> {
            user.updateRefreshToken(null); //DB에들어있는 리프레쉬토큰 지우기
        });
        return jwtProvider.deleteRefreshTokenCookie();
    }

    @Transactional(readOnly = true)
    public String reissueAccessToken(String refreshToken){
        if(!jwtProvider.validateToken(refreshToken)){
            throw new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다. 다시 로그인해주세요.");
        }

        Long userId = jwtProvider.getUserId(refreshToken);
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다. 다시 로그인해주세요."));

        if(!refreshToken.equals(user.getRefreshToken())){
            throw new IllegalArgumentException("유효하지 않은 토큰입니다. 다시 로그인해주세요.");
        }
        return jwtProvider.createAccessToken(user.getId(),user.getRole());
    }


    @Transactional
    public boolean isEmailDuplicate(String email,String provider) {
        return usersRepository.existsByLoginEmailAndProvider(email,provider);
    }

    public UserInfoResponse getMe(Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지않는 유저입니다."));
        return UserInfoResponse.builder()
                .id(user.getId())
                .loginEmail(user.getLoginEmail())
                .nickname(user.getNickname())
                .provider(user.getProvider())
                .role(user.getRole())
                .profileUrl(user.getProfileUrl())
                .location(user.getLocation())
                .ageGroup(user.getAgeGroup())
                .gender(user.getGender())
                .build();

    }
}
