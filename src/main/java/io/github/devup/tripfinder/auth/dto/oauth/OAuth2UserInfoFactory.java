package io.github.devup.tripfinder.auth.dto.oauth;

import java.util.Locale;
import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String,Object> attributes){
        return switch (registrationId.toLowerCase()){
            case "google" -> new GoogleUserInfo(attributes);
            case "kakao" -> new KakaoUserInfo(attributes);
            case "naver" -> new NaverUserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 가입방법입니다. 방법: " + registrationId);
        };
    }
}
