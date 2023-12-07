package com.car.predict.controller;

import com.car.predict.dto.external.ExceptionResponse;
import com.car.predict.exception.DtoValidationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.stream.Collectors;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(value = {DtoValidationException.class})
  protected ResponseEntity<ExceptionResponse> handleValidation(
      DtoValidationException e,
      WebRequest request
  ) {
    return new ResponseEntity<>(
        ExceptionResponse.builder()
            .timestamp(new Date())
            .status(HttpStatus.BAD_REQUEST.value())
            .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .cause(e.getError().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage
                    )
                )
            )
            .path(((ServletWebRequest) request).getRequest().getRequestURI())
            .build(),
        HttpStatus.BAD_REQUEST
    );
  }
}
