package org.wrapper.util;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import java.time.Duration;

public class RateLimitConfig {

  private final static int TOKENS_NUM = 3;

  public static Bucket createNewBucket() {
    Bandwidth limit = Bandwidth.builder()
        .capacity(TOKENS_NUM)
        .refillGreedy(TOKENS_NUM, Duration.ofMinutes(1))
        .initialTokens(1)
        .build();
    return Bucket.builder()
        .addLimit(limit)
        .build();
  }
}

