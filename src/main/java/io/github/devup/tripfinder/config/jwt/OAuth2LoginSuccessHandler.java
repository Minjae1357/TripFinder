package io.github.devup.tripfinder.config.jwt;

import io.github.devup.tripfinder.auth.dto.oauth.CustomOAuth2User;
import io.github.devup.tripfinder.auth.repository.UsersRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final UsersRepository  usersRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException
    {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        if(principal.isNewUser()){
            System.out.println("여기는신규유저");
            String redirectUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/signup")
                    .queryParam("social",true)
                    .queryParam("email",principal.getEmail())
                    .queryParam("provider",principal.getProvider())
                    .queryParam("socialUid", principal.getSocialUId())
                    .build().encode(StandardCharsets.UTF_8).toUriString();
            response.sendRedirect(redirectUrl);
            return; //디버깅으로찾은 리턴을빠트렷음
        }
        Long userId = principal.getUserId();
        String role = principal.getRole();
        System.out.println("여기는 기존유저");
        String accessToken = jwtProvider.createAccessToken(userId,role);
        String refreshToken = jwtProvider.createRefreshToken(userId);
        // DB에도 저장해서 재발급 시 대조
        usersRepository.findById(userId).ifPresent(user -> {
            user.updateRefreshToken(refreshToken);
            usersRepository.save(user);
        });
        // 쿠키를 헤더에 추가함
        ResponseCookie cookie = jwtProvider.createRefreshTokenCookie(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        // 성공시 리다이렉트
        response.sendRedirect("http://localhost:5173/oauth2/success?token="+accessToken);
    }

}
