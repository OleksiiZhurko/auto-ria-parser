package org.wrapper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wrapper.interceptor.RateLimitInterceptor;
import org.wrapper.service.BucketService;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Bean
  public BucketService bucketService() {
    return new BucketService();
  }

  @Bean
  public RateLimitInterceptor rateLimitInterceptor(BucketService bucketService) {
    return new RateLimitInterceptor(bucketService);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(rateLimitInterceptor(bucketService()));
  }
}
