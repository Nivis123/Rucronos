package com.ruchronos.manager.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix="ruchronos")
public record AppProperties(String version, Auth auth, Health health) {
 public record Auth(String mode, String issuerUrl, String clientId, long accessTokenTtlSeconds, long refreshTokenTtlSeconds, String jwtSecret) {}
 public record Health(boolean k8sConnected) {}
}
