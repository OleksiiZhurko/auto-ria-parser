package com.car.predict.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.car.predict.db.interfaces.Storage;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InMemoryTest {

  @Autowired
  Storage<String> storage;

  @AfterEach
  public void cleanUpEach() {
    storage.clear();
  }

  @Test
  void putWhenValidSingleValue() {
    final String key = "key";
    final String value = "value";

    final String expected = "value";
    final String actual = storage.put(key, value).orElse(StringUtils.EMPTY);

    assertEquals(expected, actual);
  }

  @Test
  void putWhenKeyIsNull() {
    assertThrows(NullPointerException.class, () -> storage.put(null, "value"));
  }

  @Test
  void putWhenValueIsNull() {
    assertThrows(NullPointerException.class, () -> storage.put("key", null));
  }

  @Test
  void putWhenTheSameValue() {
    final String key = "key";
    final String value = "value";

    final String expected = "value";
    String actual = storage.put(key, value).orElse(StringUtils.EMPTY);

    assertEquals(expected, actual);
    assertEquals(expected, storage.put(key, value).orElse(StringUtils.EMPTY));
  }

  @Test
  void putWhenAnotherValue() {
    final String key = "key";
    String value = "value";

    String expected = "value";
    String actual = storage.put(key, value).orElse(StringUtils.EMPTY);

    assertEquals(expected, actual);

    value = "value2";
    expected = "value2";
    actual = storage.put(key, value).orElse(StringUtils.EMPTY);

    assertEquals(expected, actual);
  }

  @Test
  void putIfAbsentWhenValidSingleValue() {
    final String key = "key";
    final String value = "value";

    final boolean actual = storage.putIfAbsent(key, value).isPresent();

    assertFalse(actual);
  }

  @Test
  void putIfAbsentWhenKeyIsNull() {
    assertThrows(NullPointerException.class, () -> storage.putIfAbsent(null, "value"));
  }

  @Test
  void putIfAbsentWhenValueIsNull() {
    assertThrows(NullPointerException.class, () -> storage.putIfAbsent("key", null));
  }

  @Test
  void putIfAbsentWhenTheSameValue() {
    final String key = "key";
    final String value = "value";

    final String expected = "value";
    boolean actual = storage.putIfAbsent(key, value).isPresent();

    assertFalse(actual);
    assertEquals(expected, storage.putIfAbsent(key, value).get());
  }

  @Test
  void putIfAbsentWhenAnotherValue() {
    final String key = "key";
    String value = "value";

    String expected = "value";

    assertFalse(storage.putIfAbsent(key, value).isPresent());

    value = "value2";

    assertEquals(expected, storage.putIfAbsent(key, value).orElse(StringUtils.EMPTY));
    assertEquals(expected, storage.retrieve(key).orElse(StringUtils.EMPTY));
  }

  @Test
  void isKeyPresentWhenEmpty() {
    assertFalse(storage.isKeyPresent("key"));
  }

  @Test
  void isKeyPresentWhenPresent() {
    final String key = "key";
    final String value = "value";

    storage.put(key, value);

    assertTrue(storage.isKeyPresent(key));
  }

  @Test
  void isKeyPresentWhenPresentAnother() {
    final String key = "key";
    final String value = "value";

    storage.put(key, value);

    assertFalse(storage.isKeyPresent("key2"));
  }

  @Test
  void retrieveWhenEmpty() {
    assertFalse(storage.retrieve("key").isPresent());
  }

  @Test
  void retrieveWhenPresent() {
    final String key = "key";
    final String value = "value";

    storage.put(key, value);

    final String expected = "value";
    final String actual = storage.retrieve(key).orElse(StringUtils.EMPTY);

    assertEquals(expected, actual);
  }

  @Test
  void retrieveWhenPresentAnother() {
    final String key = "key";
    final String value = "value";

    storage.put(key, value);

    assertFalse(storage.retrieve("key2").isPresent());
  }

  @Test
  void deleteWhenEmpty() {
    assertFalse(storage.delete("key").isPresent());
    assertEquals(0, storage.size());
  }

  @Test
  void deleteWhenPresent() {
    final String key = "key";
    final String value = "value";

    storage.put(key, value);

    final String expected = "value";

    assertEquals(expected, storage.delete(key).orElse(StringUtils.EMPTY));
    assertEquals(0, storage.size());
  }

  @Test
  void deleteWhenPresentAnother() {
    final String key = "key";
    final String value = "value";

    storage.put(key, value);

    assertFalse(storage.delete("key2").isPresent());
    assertEquals(1, storage.size());
  }
}
