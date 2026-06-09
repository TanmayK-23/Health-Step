package com.healthstep.service;

import com.healthstep.model.User;
import com.healthstep.repository.UserRepository;
import com.healthstep.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final UserRepository repo;
  private final PasswordEncoder encoder;
  private final JwtUtil jwt;

  public AuthService(UserRepository repo, PasswordEncoder encoder, JwtUtil jwt) {
    this.repo = repo; this.encoder = encoder; this.jwt = jwt;
  }

  public record AuthResult(Long userId, String username, String token) {}

  public AuthResult signup(String username, String password) {
    if (repo.existsByUsername(username)) throw new IllegalArgumentException("Username already exists");
    User u = new User();
    u.setUsername(username);
    u.setPasswordHash(encoder.encode(password));
    u = repo.save(u);
    return new AuthResult(u.getId(), u.getUsername(), jwt.generate(u.getUsername(), u.getId()));
  }

  public AuthResult login(String username, String password) {
    User u = repo.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    if (!encoder.matches(password, u.getPasswordHash())) throw new IllegalArgumentException("Invalid credentials");
    return new AuthResult(u.getId(), u.getUsername(), jwt.generate(u.getUsername(), u.getId()));
  }
}