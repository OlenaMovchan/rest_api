package com.example.rest_api.handler;

import com.example.rest_api.handler.dto.InvalidFieldDto;
import com.example.rest_api.handler.dto.ResponseExceptionDto;
import com.example.rest_api.handler.dto.ResponseValidationExceptionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class AppExceptionHandler extends ResponseEntityExceptionHandler {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        HttpStatus httpstatus = HttpStatus.BAD_REQUEST;
        List<InvalidFieldDto> errors = exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> new InvalidFieldDto()
                        .setField(((FieldError) error).getField())
                        .setMessage(error.getDefaultMessage()))
                .toList();

        ResponseValidationExceptionDto responseBody = createResponseValidationExceptionDto(
                httpstatus.value(),
                "Validation failed!",
                1,
                errors
        );
        log.error("Validation failed - {}", errors, exception);
        return handleExceptionInternal(exception, responseBody, headers, httpstatus, request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException exception,
                                                                WebRequest request) {
        ResponseExceptionDto responseBody = createResponseExceptionDto(
                exception.getStatusCode().value(),
                exception.getReason(),
                2);

        log.error("{}", responseBody.getMessage(), exception);
        return handleExceptionInternal(exception, responseBody, new HttpHeaders(), exception.getStatusCode(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception exception,
                                                      WebRequest request) {
        ResponseExceptionDto responseBody = createResponseExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                5000);
        log.error("Unknown exception...", exception);
        return handleExceptionInternal(exception, responseBody, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    private ResponseExceptionDto createResponseExceptionDto(int status,
                                                            String message,
                                                            int code) {
        return new ResponseExceptionDto()
                .setCode(code)
                .setStatus(status)
                .setMessage(message)
                .setTimestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    private ResponseValidationExceptionDto createResponseValidationExceptionDto(int status,
                                                                                String message,
                                                                                int code,
                                                                                List<InvalidFieldDto> errors) {
        return new ResponseValidationExceptionDto()
                .setCode(code)
                .setStatus(status)
                .setMessage(message)
                .setTimestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .setErrors(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleInvalidAge(
            IllegalArgumentException exception, WebRequest request) {
        log.error("Illegal argument exception. User must be at least 18 years old to register.", exception);

        ResponseExceptionDto responseBody = createResponseExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                exception.getMessage(),
                3);

        return handleExceptionInternal(exception, responseBody, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}

