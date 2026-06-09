package com.healthstep.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;

  public JwtAuthFilter(JwtUtil jwtUtil){
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    String auth = req.getHeader("Authorization");
    if(auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7);
      try {
        Claims c = jwtUtil.parse(token);
        String username = c.getSubject();
        Long uid = c.get("uid", Long.class);
        req.setAttribute("uid", uid);

        if (SecurityContextHolder.getContext().getAuthentication() == null && username != null) {
          var authToken = new UsernamePasswordAuthenticationToken(
              username, null, Collections.emptyList());
          // attach userId into request for controllers if needed
          req.setAttribute("uid", uid);
          SecurityContextHolder.getContext().setAuthentication(authToken);
        }
      } catch (Exception ignored) {}
    }
    chain.doFilter(req, res);
  }
}