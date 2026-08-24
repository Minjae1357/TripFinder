package io.github.devup.tripfinder.config.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException
    {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Long userId = ((Number) oAuth2User.getAttribute("userid")).longValue();// CustomOAuth2UserService에서 심어둔 값
        String role = authentication.getAuthorities().iterator().next().getAuthority().toString();

        String accessToken = jwtProvider.createAccessToken(userId,role);
        response.sendRedirect("http://localhost:5173/outh/success?token="+accessToken);
    }

}
