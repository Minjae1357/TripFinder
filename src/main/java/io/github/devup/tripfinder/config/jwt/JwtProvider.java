package io.github.devup.tripfinder.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


@Slf4j
@Component
public class JwtProvider {
    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;


    //이방식으로 yaml파일에 들어있는 jwt 값을가져옴
    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    //토큰생성
    public String createAccessToken(Long userId,String role){
        return createToken(userId,role,accessTokenExpiration);
    }

    public String createRefreshToken(Long userId){
        return createToken(userId,null,refreshTokenExpiration);
    }

    public String createToken(Long userId,String role, long expiration){
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(userId)) //JWT의 sub 클레임 누구꺼인지
                .issuedAt(now)  // iat = 발급시간
                .expiration(new Date(now.getTime() + expiration)) // 만료시간
                .signWith(key); // 위에서만든 키로서명하는거
        if(role!=null) builder.claim("role",role); //커스텀클레임
        return builder.compact(); // "xxxxx.yyyyy.zzzzz" 형태로 직렬화시킴
    }

    public Long getUserId(String token){
        return Long.valueOf(parseClaims(token).getSubject()); //sub꺼내서 long으로
    }
    public String getRole(String token){
        return parseClaims(token).get("role",String.class); //키를꺼냄 없으면 null
    }
    public boolean validateToken(String token){
        try{
            parseClaims(token); //파싱이 성공하면 아직유효하다는거(서명이나,토큰만료기간이아직안됨)
            return true;
        }catch (JwtException|IllegalArgumentException e){
            return false;  // 만료(ExpiredJwtException)도 JwtException의 하위클래스라 여기서 다 걸림
        }
    }

    public Claims parseClaims(String token){
        return Jwts.parser()
                .verifyWith(key) //이키로 검증하면서
                .build()
                .parseSignedClaims(token) //파싱 (서명이안맞으면 여기서 예외가터짐)
                .getPayload(); //실제 클레임 (sub,iat,exp,role 등등)
    }
}
