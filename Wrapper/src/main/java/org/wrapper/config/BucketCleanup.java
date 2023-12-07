package org.wrapper.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.wrapper.service.BucketService;


@Configuration
@EnableScheduling
public class BucketCleanup {

  private static final Logger log = LoggerFactory.getLogger(BucketCleanup.class);

  private final BucketService bucketService;

  public BucketCleanup(BucketService bucketService) {
    this.bucketService = bucketService;
  }

  @Scheduled(fixedDelay = 3600000)
  public void cleanupBuckets() {
    log.info("Cleared buckets");
    bucketService.cleanup();
  }
}
