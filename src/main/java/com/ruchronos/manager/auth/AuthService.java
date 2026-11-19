package com.ruchronos.manager.auth;
import com.ruchronos.manager.config.AppProperties;import com.ruchronos.manager.problem.ApiException;import org.springframework.http.HttpStatus;import org.springframework.stereotype.Service;import java.util.*;
@Service public class AuthService {
 private final AppProperties p;private final TokenService t;private static final Map<String,String> USERS=Map.of("admin","admin","operator","operator","viewer","viewer");
 public AuthService(AppProperties p,TokenService t){this.p=p;this.t=t;}
 public AuthDtos.AuthConfig config(){String mode=p.auth().mode().toLowerCase();return new AuthDtos.AuthConfig(mode,mode.equals("oidc")?p.auth().issuerUrl():null,mode.equals("oidc")?p.auth().clientId():null,mode.equals("oidc")?new AuthDtos.Oidc(p.auth().issuerUrl(),p.auth().clientId(),List.of("openid","profile","email")):null);}
 public AuthDtos.TokenPair login(AuthDtos.LoginRequest r){if(!"mock".equalsIgnoreCase(p.auth().mode()))throw new ApiException(HttpStatus.NOT_FOUND,"AUTH_MODE_OIDC","Вход по паролю выключен в режиме OIDC");if(!Objects.equals(USERS.get(r.username()),r.password()))throw new ApiException(HttpStatus.UNAUTHORIZED,"INVALID_CREDENTIALS","Неверный логин или пароль");String role=r.role()==null||r.role().isBlank()?r.username():r.role().toLowerCase();if(!List.of("viewer","operator","admin").contains(role))throw ApiException.badRequest("INVALID_ROLE","Допустимые роли: viewer, operator, admin");return t.create(user(r.username(),role));}
 public AuthDtos.TokenPair refresh(String s){return t.refresh(s);}public void logout(String s){t.logout(s);}
 public static UserDto user(String name,String role){String display=switch(name){case "admin"->"Administrator";case "operator"->"Operator";default->"Viewer";};return new UserDto(name,name,display,name+"@ruchronos.local",List.of(role));}
}
