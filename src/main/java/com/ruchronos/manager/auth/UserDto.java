package com.ruchronos.manager.auth;
import java.util.List;
public record UserDto(String id,String username,String displayName,String email,List<String> roles) {}
