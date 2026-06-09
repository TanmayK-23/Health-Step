package com.healthstep.controller;

import com.healthstep.service.AuthService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService auth;
  public AuthController(AuthService auth){ this.auth = auth; }

  public record AuthRequest(@NotBlank String username, @NotBlank String password) {}
  public record AuthResponse(Long userId, String username, String token) {}

  @PostMapping("/signup")
  public ResponseEntity<AuthResponse> signup(@RequestBody AuthRequest req){
    var res = auth.signup(req.username().trim(), req.password());
    return ResponseEntity.ok(new AuthResponse(res.userId(), res.username(), res.token()));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req){
    var res = auth.login(req.username().trim(), req.password());
    return ResponseEntity.ok(new AuthResponse(res.userId(), res.username(), res.token()));
  }
}