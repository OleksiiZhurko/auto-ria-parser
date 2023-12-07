package com.car.predict.dto.external;

import com.car.predict.enums.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Getter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse extends Response {

  private ProcessingStatus processingStatus;
  private List<Map<String, Object>> collection;
}
