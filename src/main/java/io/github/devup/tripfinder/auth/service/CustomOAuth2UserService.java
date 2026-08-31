package io.github.devup.tripfinder.auth.service;

import io.github.devup.tripfinder.auth.dto.oauth.CustomOAuth2User;
import io.github.devup.tripfinder.auth.dto.oauth.OAuth2UserInfo;
import io.github.devup.tripfinder.auth.dto.oauth.OAuth2UserInfoFactory;
import io.github.devup.tripfinder.auth.entity.Users;
import io.github.devup.tripfinder.auth.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final AuthService authService;
    private final UsersRepository usersRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        Optional<Users> existingUser = usersRepository.findByProviderAndSocialUid(userInfo.getProvider(),userInfo.getProviderId());

        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        if(existingUser.isPresent()){
            Users user = existingUser.get();
            return new CustomOAuth2User(user.getId(),
                    user.getRole(), userInfo.getProvider(),
                    userInfo.getProviderId(), user.getLoginEmail(),
                    false,
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_"+user.getRole())),
                    oAuth2User.getAttributes(),nameAttributeKey);
        };

        // DefaultOAuth2User -> CustomOAuth2User로 교체
        // userId, role을 별도 필드로 담아서, SuccessHandler에서 attributes 파싱 없이 바로 꺼내 쓸 수 있게 함
        return new CustomOAuth2User(
                null, null, userInfo.getProvider(), userInfo.getProviderId(), userInfo.getEmail(),
                true,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_GUEST")),
                oAuth2User.getAttributes(),
                nameAttributeKey
        );
    }
}