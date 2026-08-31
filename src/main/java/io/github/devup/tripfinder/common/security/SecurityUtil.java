package io.github.devup.tripfinder.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    // 현재 로그인한 유저의 id를 꺼내옴. 로그인 안했으면 예외
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication.getPrincipal() == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return (Long) authentication.getPrincipal();
    }
}
