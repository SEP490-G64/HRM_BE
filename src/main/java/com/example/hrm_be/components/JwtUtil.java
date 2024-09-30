package com.example.hrm_be.components;

import com.example.hrm_be.configs.objects.HrmJwtProp;
import com.example.hrm_be.models.dtos.User;
import com.example.hrm_be.models.entities.PasswordResetTokenEntity;
import com.example.hrm_be.repositories.PasswordTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtUtil {

  @Autowired PasswordTokenRepository passwordTokenRepository;
  private final HrmJwtProp hrmJwtProp;

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return doGenerateToken(claims, userDetails.getUsername());
  }

  public String generateToken(User userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("information", userDetails);
    return doGenerateToken(claims, userDetails.getEmail());
  }

  public String generateToken(String email) {
    Map<String, Object> claims = new HashMap<>();
    return doGenerateToken(claims, email);
  }

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    return !isTokenExpired(token) && userDetails.getUsername().equals(extractEmail(token));
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(hrmJwtProp.getSecret().getBytes(StandardCharsets.UTF_8))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private String doGenerateToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + hrmJwtProp.getExpiration()))
        .signWith(Keys.hmacShaKeyFor(hrmJwtProp.getSecret().getBytes(StandardCharsets.UTF_8)))
        .compact();
  }

  public String validatePasswordResetToken(String token) {
    final PasswordResetTokenEntity passToken = passwordTokenRepository.findByToken(token);

    return !isTokenFound(passToken) ? "invalidToken" : isTokenExpired(passToken) ? "expired" : null;
  }

  private boolean isTokenFound(PasswordResetTokenEntity passToken) {
    return passToken != null;
  }

  private boolean isTokenExpired(PasswordResetTokenEntity passToken) {
    final Calendar cal = Calendar.getInstance();
    return passToken.getExpiryDate().before(cal.getTime());
  }
}
