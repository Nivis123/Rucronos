package com.ruchronos.manager.auth;
import com.ruchronos.manager.config.AppProperties;
import com.ruchronos.manager.problem.ApiException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets; import java.time.Instant; import java.util.*; import java.util.concurrent.ConcurrentHashMap;
@Service
public class TokenService {
 private final AppProperties props; private final Map<String,Session> refreshSessions=new ConcurrentHashMap<>(), accessSessions=new ConcurrentHashMap<>(); private javax.crypto.SecretKey key;
 public TokenService(AppProperties p){props=p;}
 @PostConstruct void init(){byte[] b=props.auth().jwtSecret().getBytes(StandardCharsets.UTF_8);if(b.length<32)throw new IllegalStateException("JWT secret must contain at least 32 bytes");key=Keys.hmacShaKeyFor(b);}
 public AuthDtos.TokenPair create(UserDto u){return issue(u,UUID.randomUUID().toString());}
 private AuthDtos.TokenPair issue(UserDto u,String sid){Instant now=Instant.now(),exp=now.plusSeconds(props.auth().accessTokenTtlSeconds());
  String access=Jwts.builder().subject(u.id()).claim("username",u.username()).claim("roles",u.roles()).claim("sid",sid).issuedAt(Date.from(now)).expiration(Date.from(exp)).signWith(key).compact();
  String refresh=UUID.randomUUID()+"."+UUID.randomUUID();Session s=new Session(u,sid,Instant.now().plusSeconds(props.auth().refreshTokenTtlSeconds()));refreshSessions.put(refresh,s);accessSessions.put(sid,s);
  return new AuthDtos.TokenPair(access,refresh,"Bearer",props.auth().accessTokenTtlSeconds(),u);
 }
 public AuthDtos.TokenPair refresh(String token){Session s=refreshSessions.remove(token);if(s==null||s.expiresAt().isBefore(Instant.now()))throw ApiException.unauthorized("Refresh-токен недействителен или истёк");accessSessions.remove(s.sid());return issue(s.user(),UUID.randomUUID().toString());}
 public void logout(String token){Session s=refreshSessions.remove(token);if(s==null)throw ApiException.unauthorized("Refresh-токен недействителен");accessSessions.remove(s.sid());}
 public AuthenticatedUser verify(String token){try{Claims c=Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();String sid=c.get("sid",String.class);Session s=accessSessions.get(sid);if(s==null||s.expiresAt().isBefore(Instant.now()))throw ApiException.unauthorized("Сессия отозвана или истекла");return new AuthenticatedUser(c.getSubject(),c.get("username",String.class),c.get("roles",List.class));}catch(ApiException e){throw e;}catch(Exception e){throw ApiException.unauthorized("JWT недействителен или истёк");}}
 public record AuthenticatedUser(String id,String username,List<String> roles) {}
 private record Session(UserDto user,String sid,Instant expiresAt) {}
}
