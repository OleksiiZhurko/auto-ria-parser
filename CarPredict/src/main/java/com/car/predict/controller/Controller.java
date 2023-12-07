package com.car.predict.controller;

import com.car.predict.core.Processor;
import com.car.predict.dto.external.FilterAccept;
import com.car.predict.dto.external.ResultResponse;
import com.car.predict.dto.external.StatusResponse;
import com.car.predict.util.ValidatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;

@Slf4j
@CrossOrigin
@RestController
public class Controller {

  private static final String REQUESTED_GET_PARAM_INFO = "Requested from the address {} along {} requestName: {}";
  private static final String REQUESTED_POST_INFO = "Requested from the address {} along {} path: {}";

  private final Processor processor;

  @Autowired
  public Controller(Processor processor) {
    this.processor = processor;
  }

  @GetMapping(path = "/get")
  public ResponseEntity<ResultResponse> get(
      @RequestParam final String requestName,
      HttpServletRequest request
  ) {
    log.info(REQUESTED_GET_PARAM_INFO, request.getRemoteAddr(), request.getRequestURI(), requestName);
    return new ResponseEntity<>(
        processor.retrieve(requestName)
            .timestamp(new Date())
            .status(HttpStatus.OK.value())
            .reason(HttpStatus.OK.getReasonPhrase())
            .path(request.getRequestURI())
            .build(),
        HttpStatus.OK
    );
  }

  @PostMapping(path = "/produce", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StatusResponse> produce(
      @Valid @RequestBody FilterAccept filter,
      Errors errors,
      HttpServletRequest request
  ) {
    log.info(REQUESTED_POST_INFO, request.getRemoteAddr(), request.getRequestURI(), filter);
    ValidatorUtil.validate(errors);
    return new ResponseEntity<>(
        processor.submit(filter)
            .timestamp(new Date())
            .status(HttpStatus.OK.value())
            .reason(HttpStatus.OK.getReasonPhrase())
            .path(request.getRequestURI())
            .build(),
        HttpStatus.OK
    );
  }
}
