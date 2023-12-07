package org.wrapper.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

@RestController
public class Controller {

  private static final Logger log = LoggerFactory.getLogger(Controller.class);

  private final String urlProtocol;
  private final String urlServer;
  private final int urlPort;

  public Controller(
      @Value("${url.protocol}") String urlProtocol,
      @Value("${url.server}") String urlServer,
      @Value("${url.port}") int urlPort
  ) {
    this.urlProtocol = urlProtocol;
    this.urlServer = urlServer;
    this.urlPort = urlPort;
  }

  @RequestMapping("/**")
  public ResponseEntity<?> wrap(
      @RequestBody(required = false) String body,
      HttpMethod method,
      HttpServletRequest request
  ) throws URISyntaxException {
    log.info(
        "Received '{}' request from IP: '{}' with User-Agent: '{}' and Path: '{}'",
        request.getMethod(),
        request.getRemoteAddr(),
        request.getHeader("User-Agent"),
        request.getServletPath()
    );
    URI uri = UriComponentsBuilder
        .fromUri(new URI(urlProtocol, null, urlServer, urlPort, null, null, null))
        .path(request.getRequestURI())
        .query(request.getQueryString())
        .build(true)
        .toUri();

    HttpHeaders headers = retrieveHeaders(request);

    try {
      return new RestTemplate()
          .exchange(uri, method, new HttpEntity<>(body, headers), String.class);
    } catch (HttpStatusCodeException e) {
      return ResponseEntity.status(e.getRawStatusCode())
          .headers(e.getResponseHeaders())
          .body(e.getResponseBodyAsString());
    } catch (ResourceAccessException e) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
  }

  private HttpHeaders retrieveHeaders(HttpServletRequest request) {
    HttpHeaders headers = new HttpHeaders();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      headers.set(headerName, request.getHeader(headerName));
    }
    return headers;
  }
}
