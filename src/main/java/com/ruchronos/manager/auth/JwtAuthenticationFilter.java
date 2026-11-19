package com.ruchronos.manager.auth;
import com.fasterxml.jackson.databind.ObjectMapper; import com.ruchronos.manager.problem.GlobalExceptionHandler;
import jakarta.servlet.*; import jakarta.servlet.http.*; import org.springframework.http.HttpHeaders; import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; import org.springframework.security.core.authority.SimpleGrantedAuthority; import org.springframework.security.core.context.SecurityContextHolder; import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.stream.Collectors;
public class JwtAuthenticationFilter extends OncePerRequestFilter {
 private final TokenService tokens;private final ObjectMapper mapper;public JwtAuthenticationFilter(TokenService t,ObjectMapper m){tokens=t;mapper=m;}
 @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
  String h=req.getHeader(HttpHeaders.AUTHORIZATION);if(h!=null&&h.startsWith("Bearer "))try{var u=tokens.verify(h.substring(7));var a=u.roles().stream().map(x->new SimpleGrantedAuthority("ROLE_"+x.toUpperCase())).collect(Collectors.toList());SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(u,null,a));}
  catch(RuntimeException e){SecurityContextHolder.clearContext();res.setStatus(401);res.setContentType("application/problem+json");mapper.writeValue(res.getOutputStream(),GlobalExceptionHandler.response(HttpStatus.UNAUTHORIZED,"UNAUTHORIZED",e.getMessage(),req).getBody());return;}
  chain.doFilter(req,res);
 }
}
