package com.nhnacademy.hexashoppingmallservice.util;

import com.nhnacademy.hexashoppingmallservice.exception.TokenNotFoundException;
import com.nhnacademy.hexashoppingmallservice.properties.JwtProperties;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtils {

    private final JwtProperties jwtProperties;

    // 헤더에서 토큰 뽑기
    public String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(jwtProperties.getHeaderString());
        if(header == null || !header.startsWith(jwtProperties.getTokenPrefix())) {
            throw new TokenNotFoundException("Jwt Token not found");
        }

        return header.substring(jwtProperties.getTokenPrefix().length() + 1);
    }

    // 토큰이 올바른지 검증
    public Boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(jwtProperties.getSecret()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException | ExpiredJwtException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            return false;
        }
    }

    // 토큰 에서 사용자 ID 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);
    }

    // JWT에서 권한 추출
    public String getRoleFromToken(String token) {

        return Jwts.parser()
                        .setSigningKey(jwtProperties.getSecret())
                .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .get("role", String.class);
    }

}