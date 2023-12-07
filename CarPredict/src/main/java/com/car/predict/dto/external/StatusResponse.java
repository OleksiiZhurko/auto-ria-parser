package com.car.predict.dto.external;

import com.car.predict.enums.ProcessingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StatusResponse extends Response {

  private ProcessingStatus processingStatus;
}
