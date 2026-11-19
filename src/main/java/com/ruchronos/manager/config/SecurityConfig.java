package com.ruchronos.manager.config;
import com.fasterxml.jackson.databind.ObjectMapper;import com.ruchronos.manager.auth.*;import com.ruchronos.manager.problem.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletResponse;import org.springframework.context.annotation.*;import org.springframework.http.*;import org.springframework.security.config.annotation.web.builders.HttpSecurity;import org.springframework.security.config.http.SessionCreationPolicy;import org.springframework.security.web.SecurityFilterChain;import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration public class SecurityConfig {
 @Bean SecurityFilterChain securityFilterChain(HttpSecurity h,TokenService t,ObjectMapper m)throws Exception{return h.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
 .authorizeHttpRequests(a->a.requestMatchers("/health","/auth/config","/auth/login","/auth/refresh").permitAll().anyRequest().authenticated())
 .exceptionHandling(e->e.authenticationEntryPoint((q,r,x)->problem(q,r,m,401,"UNAUTHORIZED","Требуется авторизация")).accessDeniedHandler((q,r,x)->problem(q,r,m,403,"FORBIDDEN","Недостаточно прав")))
 .addFilterBefore(new JwtAuthenticationFilter(t,m),UsernamePasswordAuthenticationFilter.class).build();}
 private static void problem(jakarta.servlet.http.HttpServletRequest q,HttpServletResponse r,ObjectMapper m,int s,String c,String d)throws java.io.IOException{r.setStatus(s);r.setHeader(HttpHeaders.CONTENT_TYPE,"application/problem+json");m.writeValue(r.getOutputStream(),GlobalExceptionHandler.response(HttpStatus.valueOf(s),c,d,q).getBody());}
}
