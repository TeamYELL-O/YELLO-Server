package com.yello.server.domain.authorization;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.time.Duration.ofDays;

import com.yello.server.domain.authorization.dto.ServiceTokenVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // 릴리즈 때 고치세요 ₩~~ >_*
//    private static final Long accessTokenValidTime = ofHours(4).toMillis();
    private static final Long accessTokenValidTime = ofDays(1).toMillis();
    private static final Long refreshTokenValidTime = ofDays(14).toMillis();

    @Value("${spring.jwt.secret}")
    private String secretKey;

    public Long getUserId(String token, String secretKey) {
        return Long.valueOf(
                Jwts.parser()
                        .setSigningKey(secretKey)
                        .parseClaimsJws(token)
                        .getBody()
                        .getId());
    }

    public boolean isExpired(String token, String secretKey) {
        Claims claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
        return false;
    }

    public boolean isRefreshToken(String token, String secretKey) {
        val header = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getHeader();

        if ("refreshToken".equals(header.get("type").toString())) {
            return true;
        }

        return false;
    }

    // access 토큰 확인
    public boolean isAccessToken(String token, String secretKey) {
        val header = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getHeader();

        if ("accessToken".equals(header.get("type").toString())) {
            return true;
        }

        return false;
    }

    // access 토큰 생성
    public String createAccessToken(Long userId, String uuid) {
        return createJwt(userId, uuid, accessTokenValidTime, "accessToken");
    }

    // refresh 토큰 생성
    public String createRefreshToken(Long userId, String uuid) {
        return createJwt(userId, uuid, refreshTokenValidTime, "refreshToken");
    }

    // access, refresh 토큰 생성
    public ServiceTokenVO createServiceToken(Long userId, String uuid) {
        return ServiceTokenVO.of(
            createAccessToken(userId, uuid),
            createRefreshToken(userId, uuid)
        );
    }

    public String createJwt(Long userId, String uuid, Long tokenValidTime, String tokenType) {
        Claims claims = Jwts.claims()
            .setSubject(uuid)
            .setId(String.valueOf(userId));

        return Jwts.builder()
                .setClaims(claims)
                .setHeaderParam("type", tokenType)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidTime))
                .signWith(HS256, secretKey)
                .compact();
    }
}
