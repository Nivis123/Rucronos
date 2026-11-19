package com.ruchronos.manager.auth;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
public final class AuthDtos {
 private AuthDtos() {}
 public record LoginRequest(@NotBlank String username,@NotBlank String password,String role) {}
 public record RefreshRequest(@NotBlank String refreshToken) {}
 public record Oidc(String issuer,String clientId,List<String> scopes) {}
 @JsonInclude(JsonInclude.Include.ALWAYS)
 public record AuthConfig(String mode,String issuerUrl,String clientId,Oidc oidc) {}
 public record TokenPair(String accessToken,String refreshToken,String tokenType,long expiresIn,UserDto user) {}
}
