package com.example.rest_api.handler.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class InvalidFieldDto {
    private String field;
    private String message;
}
