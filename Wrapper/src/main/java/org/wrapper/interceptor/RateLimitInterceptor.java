package org.wrapper.interceptor;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.wrapper.service.BucketService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RateLimitInterceptor implements HandlerInterceptor {

  private final BucketService bucketService;

  public RateLimitInterceptor(BucketService bucketService) {
    this.bucketService = bucketService;
  }

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) {
    String userId = getIpFromRequest(request);
    Bucket bucket = bucketService.resolveBucket(userId);
    ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
    if (!probe.isConsumed()) {
      long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
      response.setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      return false;
    }

    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      Exception e
  ) {
    if (response.getStatus() >= HttpStatus.BAD_REQUEST.value()) {
      String userId = getIpFromRequest(request);
      Bucket bucket = bucketService.resolveBucket(userId);
      if (bucket != null) {
        bucket.addTokens(1);
      }
    }
  }

  private String getIpFromRequest(HttpServletRequest request) {
    return request.getRemoteAddr();
  }
}

