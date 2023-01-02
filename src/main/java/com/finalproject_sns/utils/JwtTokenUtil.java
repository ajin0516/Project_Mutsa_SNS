package com.finalproject_sns.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    // Token 정보 얻어내기
    private static Claims extractClaims(String token, String key) { // map
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody(); // secretKey로 token parser
    }

    public static String getUserName(String token, String Key) {
        return extractClaims(token, Key).get("userName").toString();
    }

    // Token의 만료일자 확인
    public static boolean isExpired(String token, String secretKey) {
        Date expiredDate = extractClaims(token,secretKey).getExpiration();
        return expiredDate.before(new Date());  // token에 있는 날짜가 현재보다 적은지
    }

    // JWT Token 생성
    public static String createToken(String userName, String key, long expireTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", userName); // key, value 형식으로 저장

        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs)) // 토큰 만료 시간
                .signWith(SignatureAlgorithm.HS256, key) // 사용할 암호화 알고리즘
                .compact();
    }
}
