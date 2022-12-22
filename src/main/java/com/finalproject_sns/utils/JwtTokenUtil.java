package com.finalproject_sns.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {

    private static Claims extractClaims(String token, String key) { // map
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody(); // secretKey로 token parser
    }

    public static String getUserName(String token, String Key) {
        return extractClaims(token, Key).get("userName").toString();
    }

    public static boolean isExpired(String token, String secretKey) {
        Date expiredDate = extractClaims(token,secretKey).getExpiration();
        return expiredDate.before(new Date());  // token에 있는 날짜가 현재보다 적은지
    }

    public static String createToken(String userName, String key, long expireTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }
}
