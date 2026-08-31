package io.github.devup.tripfinder.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponse {
    private Long id;
    private String loginEmail;
    private String nickname;
    private String provider;
    private String role;
    private String profileUrl;
    private String location;
    private String ageGroup;
    private Integer gender;
}
