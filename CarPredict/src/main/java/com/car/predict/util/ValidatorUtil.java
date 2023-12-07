package com.car.predict.util;

import com.car.predict.exception.DtoValidationException;
import org.springframework.validation.Errors;

import java.util.stream.Collectors;

public class ValidatorUtil {

  public static void validate(final Errors errors) {
    if (errors.hasErrors()) {
      throw new DtoValidationException(errors, "Invalid fields: " +
          errors.getFieldErrors().stream()
              .map(field -> field.getField() + "=" + field.getRejectedValue())
              .collect(Collectors.joining(", ")));
    }
  }
}
