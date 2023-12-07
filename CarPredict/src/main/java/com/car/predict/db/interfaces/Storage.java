package com.car.predict.db.interfaces;

import java.util.Optional;

public interface Storage<T> {

  Optional<T> put(final String key, final T value);
  Optional<T> putIfAbsent(final String key, final T value);
  boolean isKeyPresent(final String key);
  Optional<T> retrieve(final String key);
  Optional<T> delete(final String key);
  void clear();
  int size();
}
