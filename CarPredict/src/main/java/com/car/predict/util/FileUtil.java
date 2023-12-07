package com.car.predict.util;

import com.car.predict.data.Data;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class FileUtil {

  private static final String DELIMITER = "/";
  private static final String PATH_TO_FILES = "resources";

  public static boolean isFileExist(final String fileName) {
    return isFileExists(preparePath(fileName, Data.JSON_EXT))
        || isFileExists(preparePath(fileName, Data.CSV_EXT));
  }

  public static void deleteFiles(final String fileName) {
    boolean deleteJson = delete(preparePath(fileName, Data.JSON_EXT));
    boolean deleteCsv = delete(preparePath(fileName, Data.CSV_EXT));

    if (deleteJson) {
      log.info("{} was deleted", preparePath(fileName, Data.JSON_EXT));
    }
    if (deleteCsv) {
      log.info("{} was deleted", preparePath(fileName, Data.CSV_EXT));
    }
  }

  public static <T> void saveJsonFile(final Collection<T> collection, final String fileName) {
    if (!collection.isEmpty()) {
      ObjectMapper mapper = new ObjectMapper();
      try {
        final Path file = Files.createFile(Path.of(preparePath(fileName, Data.JSON_EXT)));
        mapper.writeValue(new File(String.valueOf(file)), collection);
      } catch (IOException | SecurityException e) {
        log.error("Unable to write processing result to the path: {}. Error: {}",
            preparePath(fileName, Data.JSON_EXT), e.getMessage());
      }
    }
  }

  public static <T> void saveCsvFile(final Collection<T> collection, final String fileName) {
    try {
      FileWriter out = new FileWriter(preparePath(fileName, Data.CSV_EXT));
      if (!collection.isEmpty()) {
        try (CSVPrinter printer = new CSVPrinter(
            out,
            CSVFormat.DEFAULT.withHeader(
                ReflectUtil.produceHeader(collection.stream().findFirst().orElse(null))
            )
        )) {
          for (T elem : collection) {
            printer.printRecord(ReflectUtil.produceValues(elem));
          }
        }
      }
    } catch (IOException | SecurityException e) {
      log.error("Unable to write processing result to the path: {}. Error: {}",
          preparePath(fileName, Data.JSON_EXT), e.getMessage());
    }
  }

  public static List<Map<String, Object>> readFile(final String fileName) {
    final String path = preparePath(fileName, Data.JSON_EXT);
    List<Map<String, Object>> collection = Collections.emptyList();

    try {
      final String json = Files.readString(Path.of(path), StandardCharsets.UTF_8);
      final ObjectMapper mapper = new ObjectMapper();
      collection = mapper.readValue(json, new TypeReference<>() {
      });
    } catch (IOException e) {
      log.warn("Unable to read: {}. Error: {}", path, e.getMessage());
    }

    return collection;
  }

  private static boolean isFileExists(final String path) {
    try {
      return new File(path).exists();
    } catch (SecurityException e) {
      log.warn("Unable to find {}. Error: {}", path, e.getMessage());
      return false;
    }
  }

  private static boolean delete(final String path) {
    try {
      return new File(path).delete();
    } catch (SecurityException e) {
      log.warn("Unable to delete {}. Error: {}", path, e.getMessage());
      return false;
    }
  }

  private static String preparePath(final String fileName, final String ext) {
    return PATH_TO_FILES + DELIMITER + fileName + ext;
  }
}
