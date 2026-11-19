package com.ruchronos.manager.problem;
import org.springframework.http.HttpStatus;
public class ApiException extends RuntimeException {
 private final HttpStatus status; private final String code;
 public ApiException(HttpStatus status,String code,String detail){super(detail);this.status=status;this.code=code;}
 public HttpStatus status(){return status;} public String code(){return code;}
 public static ApiException unauthorized(String d){return new ApiException(HttpStatus.UNAUTHORIZED,"UNAUTHORIZED",d);}
 public static ApiException notFound(String d){return new ApiException(HttpStatus.NOT_FOUND,"NOT_FOUND",d);}
 public static ApiException badRequest(String c,String d){return new ApiException(HttpStatus.BAD_REQUEST,c,d);}
}
