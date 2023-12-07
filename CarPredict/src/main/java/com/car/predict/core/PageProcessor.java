package com.car.predict.core;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PageProcessor {

  private static final int RETRIES = 7;
  private static final int TIMEOUT = 1000;
  private static final String URL = "https://auto.ria.com/uk/search/"
      + "?indexName=auto,order_auto,newauto_search"
      + "&categories.main.id=1"
      + "&country.import.usa.not=-1"
      + "&price.currency=1"
      + "&sort[0].order=price.asc"
      + "&abroad.not=0"
      + "&custom.not=1"
      + "&damage.not=1"
      + "&page=%d"
      + "&size=100";

  public List<String> getCarPages(final int start, final int step, final int pages) {
    final List<String> links = new ArrayList<>();
    Optional<Document> documentOpt;
    List<String> tempLinks;

    for (int i = start; i < pages; i+=step) {
      documentOpt = getDocument(String.format(URL, i));

      if (documentOpt.isPresent()) {
        tempLinks = getLinks(documentOpt.get());
        if (tempLinks.isEmpty()) break;
        links.addAll(tempLinks);
      }
    }

    return links;
  }

  public Optional<Document> getDocument(final String url) {
    for (int one = 0; one < RETRIES; one++) {
      try {
        return Optional.of(
            Jsoup.connect(url)
                .ignoreContentType(true)
                .get()
                .connection()
                .get()
        );
      } catch (Exception e) {
        e.printStackTrace();
        try {
          Thread.sleep(one * TIMEOUT);
        } catch (InterruptedException ignored) { }
      }
    }

    log.warn("Failed to get info: " + url);
    return Optional.empty();
  }

  private List<String> getLinks(final Document document) {
    return document.getElementsByClass("ticket-item").stream()
        .map(elem -> elem.getElementsByClass("item ticket-title"))
        .map(elements -> elements.select("a"))
        .map(elem -> elem.attr("href"))
        .collect(Collectors.toList());
  }
}
