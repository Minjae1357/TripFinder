package io.github.devup.tripfinder.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SignupRequest {
    private String loginEmail;
    private String loginPassword;
    private String provider;
    private String nickname;
    private String location;
    private String ageGroup;
    private Integer gender;
}
