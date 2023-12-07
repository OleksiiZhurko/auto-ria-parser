package com.car.predict.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FilterAccept {

  @Setter
  @ToString.Exclude
  @NotNull(message = "Must not be null")
  private String id = "id";

  @NotNull(message = "Must not be null")
  @Min(value = 1, message = "Must be greater than 0")
  @Max(value = 100_000, message = "Must be less than 100_000")
  private Integer threads = 2;

  @NotNull(message = "Must not be null")
  @Min(value = 0, message = "Must be greater than 0")
  @Max(value = Integer.MAX_VALUE, message = "Must be less than 2147483648")
  private Integer pages = Integer.MAX_VALUE;

  @NotNull(message = "Must not be null")
  private Boolean generateCsv = false;

  @NotNull(message = "Must not be null")
  private Boolean force = false;
}


