package com.car.predict.core;

import com.car.predict.db.interfaces.Storage;
import com.car.predict.dto.external.FilterAccept;
import com.car.predict.dto.external.ResultResponse;
import com.car.predict.dto.external.ResultResponse.ResultResponseBuilder;
import com.car.predict.dto.external.StatusResponse;
import com.car.predict.dto.external.StatusResponse.StatusResponseBuilder;
import com.car.predict.dto.internal.Car;
import com.car.predict.dto.internal.Processing;
import com.car.predict.enums.ProcessingStatus;
import com.car.predict.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Processor {

  private final CarProcessor carProcessor;
  private final Storage<Processing<?>> storage;

  @Autowired
  public Processor(CarProcessor carProcessor, Storage<Processing<?>> storage) {
    this.carProcessor = carProcessor;
    this.storage = storage;
  }

  public StatusResponseBuilder<?, ?> submit(final FilterAccept filterAccept) {
    final StatusResponseBuilder<?, ?> builder = StatusResponse.builder();

    if (storage.isKeyPresent(filterAccept.getId())) {
      builder.processingStatus(ProcessingStatus.PREPARING);
    } else if (!filterAccept.getForce() && FileUtil.isFileExist(filterAccept.getId())) {
      builder.processingStatus(ProcessingStatus.COMPLETED);
    } else {
      if (filterAccept.getForce() && FileUtil.isFileExist(filterAccept.getId())) {
        FileUtil.deleteFiles(filterAccept.getId());
      }

      Processing<Car> carProcessing =
          new Processing<>(filterAccept.getId(), ProcessingStatus.ACCEPTED);
      storage.put(filterAccept.getId(), carProcessing);
      carProcessor.submit(filterAccept, carProcessing);
      builder.processingStatus(ProcessingStatus.ACCEPTED);
    }

    return builder;
  }

  public ResultResponseBuilder<?, ?> retrieve(final String id) {
    final ResultResponseBuilder<?, ?> builder = ResultResponse.builder();

    if (storage.isKeyPresent(id)) {
      builder.processingStatus(ProcessingStatus.PREPARING);
    } else if (FileUtil.isFileExist(id)) {
      builder.processingStatus(ProcessingStatus.COMPLETED);
      builder.collection(FileUtil.readFile(id));
    } else {
      builder.processingStatus(ProcessingStatus.ABSENT);
    }

    return builder;
  }
}
