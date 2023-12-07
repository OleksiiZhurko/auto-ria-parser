package org.wrapper.service;

import io.github.bucket4j.Bucket;
import org.wrapper.dto.Pair;
import org.wrapper.util.RateLimitConfig;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class BucketService {

  private final ConcurrentHashMap<String, Pair<Bucket, Long>> buckets = new ConcurrentHashMap<>();

  public Bucket resolveBucket(String userId) {
    return buckets.compute(userId, (key, pair) -> {
      if (pair == null || isExpired(pair.getRight())) {
        return new Pair<>(RateLimitConfig.createNewBucket(), System.currentTimeMillis());
      }
      pair.setRight(System.currentTimeMillis());
      return pair;
    }).getLeft();
  }

  public void cleanup() {
    buckets.entrySet().removeIf(entry -> isExpired(entry.getValue().getRight()));
  }

  private boolean isExpired(long lastAccessTime) {
    return System.currentTimeMillis() - lastAccessTime > TimeUnit.HOURS.toMillis(1);
  }
}
