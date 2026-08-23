package io.github.devup.tripfinder.auth.dto.oauth;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{
    private final Map<String,Object> attributes;

    public NaverUserInfo(Map<String,Object> attributes){
        this.attributes = attributes;
    }


    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("name");
    }

    @Override
    public String getProfileUrl() {
        return (String) attributes.get("profile_image");
    }
}
