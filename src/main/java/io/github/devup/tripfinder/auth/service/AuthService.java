package io.github.devup.tripfinder.auth.service;

import io.github.devup.tripfinder.auth.dto.oauth.OAuth2UserInfo;
import io.github.devup.tripfinder.auth.dto.request.SignupRequest;
import io.github.devup.tripfinder.auth.entity.Users;
import io.github.devup.tripfinder.auth.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UsersRepository usersRepository;

    public Users signup(SignupRequest signupRequest) {
        Users user = Users.builder()
                .loginEmail(signupRequest.getLoginEmail())
                .loginPassword(signupRequest.getLoginPassword())
                .provider("local")
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
                .orElseGet(() ->{
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
}
