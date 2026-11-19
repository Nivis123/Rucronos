package com.ruchronos.manager.auth;
import jakarta.validation.Valid;import org.springframework.http.ResponseEntity;import org.springframework.web.bind.annotation.*;
@RestController public class AuthController {private final AuthService s;public AuthController(AuthService s){this.s=s;}
 @GetMapping("/auth/config") public AuthDtos.AuthConfig config(){return s.config();}
 @PostMapping("/auth/login") public AuthDtos.TokenPair login(@Valid @RequestBody AuthDtos.LoginRequest r){return s.login(r);}
 @PostMapping("/auth/refresh") public AuthDtos.TokenPair refresh(@Valid @RequestBody AuthDtos.RefreshRequest r){return s.refresh(r.refreshToken());}
 @PostMapping("/auth/logout") public ResponseEntity<Void> logout(@Valid @RequestBody AuthDtos.RefreshRequest r){s.logout(r.refreshToken());return ResponseEntity.noContent().build();}
}
