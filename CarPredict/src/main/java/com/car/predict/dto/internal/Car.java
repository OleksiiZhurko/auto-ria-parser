package com.car.predict.dto.internal;

import com.car.predict.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;

@Slf4j
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class Car {

  private static final String[] FIELDS = ReflectUtil.produceHeader(Car.builder().build());

  private String producer;
  private String model;
  private String body;
  private String drive;
  private String transmission;
  private Double engine;
  private Double horsepower;
  private Double kW;
  private String fuel;
  private Double distance;
  private Integer owners;
  private Integer year;
  private Integer price;
  private String color;
  private String city;
  private Boolean enabled;
  private String link;

  public static final Comparator<Car> comparator = Comparator.comparingInt(Car::getPrice)
      .thenComparing(Car::getProducer)
      .thenComparing(Car::getModel)
      .thenComparingDouble(Car::getDistance)
      .thenComparing(Car::toString);
}
