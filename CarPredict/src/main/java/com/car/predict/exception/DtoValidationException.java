package com.car.predict.exception;

import org.springframework.validation.Errors;

public class DtoValidationException extends RuntimeException {

  private final Errors error;

  public DtoValidationException(Errors errors, String errMsg) {
    super(errMsg);
    this.error = errors;
  }

  public Errors getError() {
    return error;
  }
}
