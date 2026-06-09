package com.healthstep.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
  private final Key key;
  private final long expMillis;

  public JwtUtil(@Value("${app.jwt.secret}") String secret,
                 @Value("${app.jwt.expMinutes}") long expMinutes) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expMillis = expMinutes * 60_000;
  }

  public String generate(String username, Long userId) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .setSubject(username)
        .claim("uid", userId)
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + expMillis))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Claims parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token).getBody();
  }
}