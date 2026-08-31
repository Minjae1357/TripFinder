package io.github.devup.tripfinder.config.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor                         //요청당 딱 한 번만 필터 로직이 실행되도록 보장합니다.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                            //HTTP 요청 헤더(Authorization)에서 Bearer로시작하는 걸찾아 토큰값만 잘라서반환하는거
        String token = resolveToken(request);
                                //유효한 토큰인지 검증하는곳
        if (token != null && jwtProvider.validateToken(token)) {
            Long userId = jwtProvider.getUserId(token);
            String role = jwtProvider.getRole(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    // Principal (사용자 식별자),Credentials(비밀번호/자격증명),Authorities (권한 목록)
                    userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
            //SecurityContextHolder에 저장 control 쪽이나 서비스쪽에서 사용가능하게만듬
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);   // 토큰 없거나 무효해도 통과 (permitAll이 걸러줌)
    }

    //이게 bearer로시작하는애찾아 토큰부분만 반환하는메서드
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

}
