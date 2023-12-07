package com.car.predict.dto.internal;

import com.car.predict.enums.ProcessingStatus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Getter
public class Processing<T> {

  private static final int COMPLETED = 100;
  private static final int DETAILING = 1;

  private final String id;
  private int totalLinks = 0;
  private int processedLinks = 0;
  private int parts = 0;
  private int detailing = 0;
  private ProcessingStatus processingStatus;
  private Set<T> cars;

  public Processing(String id, ProcessingStatus processingStatus) {
    this.id = id;
    setProcessingStatus(processingStatus);
  }

  public void setTotalLinks(int totalLinks) {
    this.totalLinks = totalLinks;
    log.info("Prepared {} car links", totalLinks);
    this.parts = this.totalLinks / COMPLETED;
  }

  public synchronized void updateProcessedLinks() {
    if (++processedLinks / parts >= detailing * DETAILING) {
      log.info("Completed {}% - {}/{}", detailing * DETAILING, processedLinks, totalLinks);
      detailing++;
    }
  }

  public void setProcessingStatus(final ProcessingStatus processingStatus) {
    this.processingStatus = processingStatus;
    log.info("Request status: {}", this.processingStatus);
  }

  public void setCars(final Comparator<T> comparator, final List<T> cars) {
    this.cars = new TreeSet<>(comparator);
    this.cars.addAll(cars);
    log.info("Added and sorted {} elements", this.cars.size());
  }
}


