package com.example.hrm_be.configs.filters;

import com.example.hrm_be.services.impl.JwtUserDetailsServiceImpl;
import com.example.hrm_be.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired JwtUtil jwtUtil;

  @Autowired JwtUserDetailsServiceImpl jwtUserDetailsService;

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return request.getServletPath().startsWith("/api/v1/auth");
  }

  @Override
  protected void doFilterInternal(
          HttpServletRequest request,
          @NonNull HttpServletResponse response,
          @NonNull FilterChain filterChain)
          throws ServletException, IOException {

    final String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    String email = null;
    String jwtToken = null;
    // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
    if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
      jwtToken = requestTokenHeader.substring(7);
      try {
        email = jwtUtil.extractEmail(jwtToken);
      } catch (IllegalArgumentException e) {
        log.error("Unable to get JWT Token");
      } catch (ExpiredJwtException e) {
        log.error("JWT Token has expired");
      }
    } else {
      log.warn("JWT Token does not begin with Bearer String");
    }

    // Once we get the token validate it.
    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

      UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(email);

      // if token is valid configure Spring Security to manually set authentication
      if (jwtUtil.validateToken(jwtToken, userDetails)) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
        // After setting the Authentication in the context, we specify
        // that the current user is authenticated. So it passes the Spring Security Configurations
        // successfully.
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}