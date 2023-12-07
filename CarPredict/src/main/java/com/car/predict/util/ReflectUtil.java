package com.car.predict.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReflectUtil {

  public static <T> String[] produceHeader(final T obj) {
    if (obj == null) {
      return new String[]{};
    }

    return Arrays.stream(obj.getClass().getDeclaredFields())
        .filter(field -> !Modifier.isStatic(field.getModifiers()))
        .map(field -> StringUtils.capitalize(field.getName()))
        .toArray(String[]::new);
  }

  public static <T> List<String> produceValues(final T obj) {
    if (obj == null) {
      return Collections.emptyList();
    }

    return Arrays.stream(obj.getClass().getDeclaredFields())
        .filter(field -> !Modifier.isStatic(field.getModifiers()))
        .map(field -> getFieldValue(obj, field))
        .collect(Collectors.toList());
  }

  private static <T> String getFieldValue(final T obj, final Field field) {
    Object result = null;
    field.setAccessible(true);

    try {
      result = field.get(obj);
    } catch (IllegalAccessException ignored) {
    }

    return result == null ? null : result.toString();
  }
}
