package com.healthstep.sync;

import com.healthstep.security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE stream: clients connect to /sync/stream/{uid}?token=JWT
 * Validates JWT using JwtUtil to ensure user identity matches the token.
 */
@RestController
@RequestMapping("/sync")
public class SyncController {

  private final SyncHub hub;
  private final JwtUtil jwtUtil;

  public SyncController(SyncHub hub, JwtUtil jwtUtil) {
    this.hub = hub;
    this.jwtUtil = jwtUtil;
  }

  @GetMapping(path = "/stream/{uid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter stream(@PathVariable long uid,
                           @RequestParam(name = "token", required = false) String token) {
    if (token == null || token.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing token");
    }

    // Allow either raw token or "Bearer <token>" in the query param
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    try {
      // Parse & validate signature/expiry
      Claims claims = jwtUtil.parse(token);  // throws if invalid or expired

      // Subject is the username (per JwtUtil.generate)
      String subject = claims.getSubject();
      if (subject == null || subject.isBlank()) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token subject");
      }

      // Enforce that the token's uid matches the path {uid}
      Object uidClaim = claims.get("uid");
      if (uidClaim == null) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Missing uid claim");
      }
      long uidFromToken;
      if (uidClaim instanceof Number n) {
        uidFromToken = n.longValue();
      } else {
        try { uidFromToken = Long.parseLong(uidClaim.toString()); }
        catch (NumberFormatException e) {
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bad uid claim");
        }
      }
      if (uidFromToken != uid) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token does not match user");
      }

      // Subscribe to SSE for this user
      SseEmitter emitter = hub.subscribe(uid);
      try {
        emitter.send(SseEmitter.event().name("hello").data("ok"));
      } catch (Exception ignored) {}
      return emitter;

    } catch (Exception e) {
      // io.jsonwebtoken will throw for bad signature or expired tokens
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token validation failed");
    }
  }
}