package io.github.devup.tripfinder.auth.dto.oauth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
@Getter
public class CustomOAuth2User implements OAuth2User {
    private final Long userId;
    private final String role;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String,Object> attributes;
    private final String nameAttributeKey;

    public CustomOAuth2User(Long userId,String role,Collection<? extends  GrantedAuthority> authorities,Map<String,Object> attributes,String nameAttributeKey){
        this.userId = userId;
        this.role = role;
        this.authorities = authorities;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get(nameAttributeKey));
    }
}
