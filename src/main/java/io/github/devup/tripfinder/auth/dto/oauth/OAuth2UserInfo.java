package io.github.devup.tripfinder.auth.dto.oauth;

public interface OAuth2UserInfo {
    String getProvider();
    String getProviderId();
    String getEmail();
    String getNickname();
    String getProfileUrl();
}
