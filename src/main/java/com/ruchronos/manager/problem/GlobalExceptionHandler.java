package com.ruchronos.manager.problem;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(ApiException.class) ResponseEntity<ApiProblem> api(ApiException e,HttpServletRequest r){return response(e.status(),e.code(),e.getMessage(),r);}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiProblem> validation(MethodArgumentNotValidException e,HttpServletRequest r){
  String d=e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).findFirst().orElse("Некорректный запрос");
  return response(HttpStatus.BAD_REQUEST,"VALIDATION_ERROR",d,r);
 }
 @ExceptionHandler(HttpMessageNotReadableException.class) ResponseEntity<ApiProblem> unreadable(HttpMessageNotReadableException e,HttpServletRequest r){return response(HttpStatus.BAD_REQUEST,"INVALID_REQUEST","Тело запроса отсутствует или содержит некорректный JSON",r);}
 @ExceptionHandler(NoResourceFoundException.class) ResponseEntity<ApiProblem> missing(NoResourceFoundException e,HttpServletRequest r){return response(HttpStatus.NOT_FOUND,"NOT_FOUND","Ресурс не найден",r);}
 @ExceptionHandler(HttpRequestMethodNotSupportedException.class) ResponseEntity<ApiProblem> method(HttpRequestMethodNotSupportedException e,HttpServletRequest r){return response(HttpStatus.METHOD_NOT_ALLOWED,"METHOD_NOT_ALLOWED","Метод не поддерживается для этого ресурса",r);}
 @ExceptionHandler(Exception.class) ResponseEntity<ApiProblem> unexpected(Exception e,HttpServletRequest r){return response(HttpStatus.INTERNAL_SERVER_ERROR,"INTERNAL_ERROR","Внутренняя ошибка сервера",r);}
 public static ResponseEntity<ApiProblem> response(HttpStatus s,String c,String d,HttpServletRequest r){
  return ResponseEntity.status(s).contentType(MediaType.valueOf("application/problem+json"))
   .body(new ApiProblem("https://ruchronos.dev/problems/"+c.toLowerCase(),s.getReasonPhrase(),s.value(),d,r.getRequestURI(),c));
 }
}
