package com.example.rest_api.handler.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ResponseValidationExceptionDto {
    private String timestamp;
    private int status;
    private String message;
    private int code;
    private List<InvalidFieldDto> errors;
}
