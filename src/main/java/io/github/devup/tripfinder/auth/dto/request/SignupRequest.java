package io.github.devup.tripfinder.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SignupRequest {
    private String loginEmail;
    private String loginPassword;
    private String provider;
    private String socialUid;   // 소셜 가입 완료 요청일 때만 값 있음
    private String nickname;
    private String location;
    private String ageGroup;
    private Integer gender;
}
