package com.ruchronos.manager.problem;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiProblem(String type, String title, int status, String detail, String instance, String code) {}
