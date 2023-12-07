package com.car.predict.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class Response {

  private Date timestamp;
  private Integer status;
  private String reason;
  private String path;
}
