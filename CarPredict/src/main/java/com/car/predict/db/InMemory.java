package com.car.predict.db;

import com.car.predict.db.interfaces.Storage;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemory<T> implements Storage<T> {

  private final ConcurrentMap<String, T> storage = new ConcurrentHashMap<>();

  @Override
  public Optional<T> put(final String key, final T value) {
    storage.put(key, value);
    return Optional.of(value);
  }

  @Override
  public Optional<T> putIfAbsent(final String key, final T value) {
    return Optional.ofNullable(storage.putIfAbsent(key, value));
  }

  @Override
  public boolean isKeyPresent(final String key) {
    return storage.containsKey(key);
  }

  @Override
  public Optional<T> retrieve(final String key) {
    return Optional.ofNullable(storage.get(key));
  }

  @Override
  public Optional<T> delete(final String key) {
    return Optional.ofNullable(storage.remove(key));
  }

  @Override
  public void clear() {
    storage.clear();
  }

  @Override
  public int size() {
    return storage.size();
  }
}
