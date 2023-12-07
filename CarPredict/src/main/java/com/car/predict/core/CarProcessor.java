package com.car.predict.core;

import com.car.predict.db.interfaces.Storage;
import com.car.predict.dto.external.FilterAccept;
import com.car.predict.dto.internal.Car;
import com.car.predict.dto.internal.Processing;
import com.car.predict.enums.ProcessingStatus;
import com.car.predict.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CarProcessor {

  private final PageProcessor pageProcessor;
  private final CarParser carParser;
  private final Storage<Processing<?>> storage;

  @Autowired
  public CarProcessor(
      PageProcessor pageProcessor,
      CarParser carParser,
      Storage<Processing<?>> storage
  ) {
    this.pageProcessor = pageProcessor;
    this.carParser = carParser;
    this.storage = storage;
  }

  public void submit(final FilterAccept filterAccept, final Processing<Car> processing) {
    CompletableFuture.runAsync(() -> {
      processing.setProcessingStatus(ProcessingStatus.PREPARING);
      final ExecutorService executor = Executors.newFixedThreadPool(filterAccept.getThreads());
      final List<String> linksToPages =
          submitAll(
              executor,
              produceInitialLinks(filterAccept.getThreads(), filterAccept.getPages()),
              filterAccept.getId()
          ).stream()
          .map(this::getFuture)
          .flatMap(Collection::stream)
          .collect(Collectors.toList());
      processing.setTotalLinks(linksToPages.size());
      processing.setProcessingStatus(ProcessingStatus.PARSING);

      final List<Car> cars = submitAll(
          executor,
          produceCars(filterAccept.getThreads(), linksToPages, processing),
          filterAccept.getId()
      ).stream()
          .map(this::getFuture)
          .flatMap(Collection::stream)
          .collect(Collectors.toList());

      executor.shutdown();
      processing.setProcessingStatus(ProcessingStatus.SORTING);
      processing.setCars(Car.comparator, cars);
      processing.setProcessingStatus(ProcessingStatus.SAVING);
      FileUtil.saveJsonFile(processing.getCars(), processing.getId());

      if (filterAccept.getGenerateCsv()) {
        FileUtil.saveCsvFile(processing.getCars(), processing.getId());
      }

      storage.delete(processing.getId());
      log.info("Request with id {} was fully processed", filterAccept.getId());
    });
  }

  private <T> List<Future<List<T>>> submitAll(
      final ExecutorService executor,
      final List<Callable<List<T>>> callables,
      final String requestId
  ) {
    try {
      return executor.invokeAll(callables);
    } catch (InterruptedException e) {
      log.error("Failed to execute request: " + requestId + ". Error: " + e.getMessage());
      return Collections.emptyList();
    }
  }

  private List<Callable<List<String>>> produceInitialLinks(
      final int threads,
      final int pages
  ) {
    final List<Callable<List<String>>> callables = new ArrayList<>();
    for (int i = 0; i < threads; i++) {
      int finalI = i;
      callables.add(() -> pageProcessor.getCarPages(finalI, threads, pages));
    }
    return callables;
  }

  private List<Callable<List<Car>>> produceCars(
      final int threads,
      final List<String> links,
      final Processing<Car> processing
  ) {
    final List<Callable<List<Car>>> callables = new ArrayList<>();

    for (int i = 0; i < threads; i++) {
      int finalI = i;
      callables.add(() -> {
        List<Car> cars = new ArrayList<>();

        for (int j = finalI; j < links.size(); j+=threads) {
          int finalJ = j;
          pageProcessor.getDocument(links.get(j))
              .flatMap(doc -> carParser.prepareCar(doc, links.get(finalJ)))
              .ifPresent(cars::add);
          processing.updateProcessedLinks();
        }

        return cars;
      });
    }

    return callables;
  }

  private <T> List<T> getFuture(final Future<List<T>> future) {
    try {
      return future.get();
    } catch (InterruptedException | ExecutionException e) {
      return List.of();
    }
  }
}
