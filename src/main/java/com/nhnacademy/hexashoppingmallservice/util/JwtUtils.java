package com.nhnacademy.hexashoppingmallservice.util;

import com.nhnacademy.hexashoppingmallservice.exception.InvalidTokenException;
import com.nhnacademy.hexashoppingmallservice.exception.TokenNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.TokenPermissionDenied;
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

    // 리퀘스트를 받고, 헤더에서 토큰 뽑기
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

    // 토큰이 올바른지 검증
    public void validateTokenOrThrow(String token) {
        if (!validateToken(token)) {
            throw new InvalidTokenException("Invalid JWT token");
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

    // 토큰에서 권한 추출 (ROLE_ADMIN, ROLE_MEMBER)
    public String getRoleFromToken(String token) {

        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecret())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // 관리자 권한 검증 메서드
    public void ensureAdmin(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        validateTokenOrThrow(token);
        String role = getRoleFromToken(token);
        if (role == null || !role.contains("ADMIN")) {
            throw new TokenPermissionDenied();
        }
    }

    // 특정 사용자와 일치하는지 검증 메서드
    public void ensureUserAccess(HttpServletRequest request, String memberId) {
        String token = getTokenFromRequest(request);
        validateTokenOrThrow(token);
        String id = getUsernameFromToken(token);
        String role = getRoleFromToken(token);
        if (role != null && role.contains("ADMIN")) {
            return;
        }
        if (id == null || !id.equals(memberId)) {
            throw new TokenPermissionDenied();
        }
    }
}