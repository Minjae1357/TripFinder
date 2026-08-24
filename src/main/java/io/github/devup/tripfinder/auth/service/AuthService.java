package io.github.devup.tripfinder.auth.service;

import io.github.devup.tripfinder.auth.dto.request.SignupRequest;
import io.github.devup.tripfinder.auth.entity.Users;
import io.github.devup.tripfinder.auth.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
