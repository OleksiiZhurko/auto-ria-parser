package com.car.predict.core;

import com.car.predict.dto.internal.Car;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class CarParser {

  private static final String DELIMITER = ";";
  private static final String DELIMITER_EMPTY = " ";
  private static final String DELIMITER_POINT = "•";
  private static final Pattern DOUBLE_CHECK = Pattern.compile("\\d+(\\.\\d+)*");
  private static final Element EMPTY_ELEMENT = new Element("empty");

  public Optional<Car> prepareCar(final Document document, final String linkTo) {
    final Element heading = document.getElementById("heading-cars");

    if (heading != null) {
      final Element first = document.getElementsByClass("panel-breadcrumbs mhide").first();

      if (first != null) {
        final Element child = getChild(first, 0);

        if (child != null) {
          int childrenSize = child.childrenSize();

          if (childrenSize > 4) {
            final List<Element> descriptions = getDescriptions(document);

            if (!descriptions.isEmpty()) {
              final String producer = getProducer(child, childrenSize, linkTo);
              final String model = getModel(child, childrenSize, linkTo);
              final Integer year = getYear(document, linkTo);
              final Integer price = getPrice(document, linkTo);

              if (producer != null && model != null && year != null && price != null) {
                final String engine = getEngine(descriptions, linkTo);

                return Optional.of(
                    Car.builder()
                        .producer(getProducer(child, childrenSize, linkTo))
                        .model(getModel(child, childrenSize, linkTo))
                        .body(getBody(descriptions.get(0)))
                        .drive(getDrive(descriptions, linkTo))
                        .transmission(getTransmission(descriptions, linkTo))
                        .engine(parseEngine(engine, linkTo))
                        .horsepower(parseHorsepower(engine, linkTo))
                        .kW(parseKW(engine, linkTo))
                        .fuel(parseFuel(engine, linkTo))
                        .distance(getDistance(descriptions, linkTo))
                        .owners(getOwners(document, linkTo))
                        .year(getYear(document, linkTo))
                        .price(getPrice(document, linkTo))
                        .color(getColor(descriptions, linkTo))
                        .city(getCity(child, childrenSize, linkTo))
                        .enabled(getEnabled(document))
                        .link(linkTo)
                        .build()
                );
              }
            }
          }
        }
      }
    }

    return Optional.empty();
  }

  private Element getChild(final Element element, int index) {
    try {
      return element.child(index);
    } catch (IndexOutOfBoundsException ignored) {
      return null;
    }
  }

  private String getProducer(final Element element, int childrenSize, final String link) {
    Element child = getChild(element, childrenSize - 2);

    if (child == null) {
      log.warn("Unable to find producer name from " + link);
      return null;
    }

    return child.text().trim();
  }

  private String getModel(final Element element, int childrenSize, final String link) {
    Element child = getChild(element, childrenSize - 1);

    if (child == null) {
      log.warn("Unable to find model from " + link);
      return null;
    }

    return child.text().trim();
  }

  private String getBody(final Element element) {
    return StringUtils.substringBefore(element.wholeText(), DELIMITER_POINT).trim();
  }

  private List<Element> getDescriptions(final Document document) {
    final Element first = document.getElementsByClass("description-car").first();

    return first == null ? Collections.emptyList() : first.getElementsByTag("dd");
  }

  private String getDrive(final List<Element> elements, final String link) {
    final String type = "drive type";

    return getString(getArgument(elements, "Привід", type, link), type, link);
  }

  private String getTransmission(final List<Element> elements, final String link) {
    final String type = "transmission type";

    return getString(getArgument(elements, "Коробка передач", type, link), type, link);
  }

  private String getEngine(final List<Element> elements, final String link) {
    final String type = "engine";
    final String engine = getString(getArgument(elements, "Двигун", type, link), type, link);

    return engine == null ? null : engine.replaceAll(" • ", DELIMITER).trim();
  }

  private Element getFirstElementByClass(final Document document, final String className) {
    return document.getElementsByClass(className).first();
  }

  private Element getFirstElementByClass(final Element element, final String className) {
    return element.getElementsByClass(className).first();
  }

  private Element getArgument(
      final List<Element> elements,
      final String eq,
      final String type,
      final String link
  ) {
    final Optional<Element> element = elements.stream()
        .filter(e -> eq.equals(ofNullable(getFirstElementByClass(e, "label"))
            .orElse(EMPTY_ELEMENT).text()))
        .findFirst();

    if (element.isEmpty()) {
      log.debug("Unable to find {} from {}", type, link);
      return null;
    }

    return getFirstElementByClass(element.get(), "argument");
  }

  private String getString(final Element element, final String type, final String link) {
    if (element == null) {
      log.debug("Unable to find {} from {}", type, link);
      return null;
    }

    return element.text().trim();
  }

  private Double parseEngine(final String engine, final String link) {
    if (engine != null) {
      final Matcher matcher = DOUBLE_CHECK.matcher(engine);

      if (matcher.find()) {
        try {
          return Double.parseDouble(matcher.group());
        } catch (NumberFormatException ignored) {
        }
      }
    }

    log.debug("Unable to find engine from " + link);
    return null;
  }

  private Double parseHorsepower(final String engine, final String link) {
    if (engine != null) {
      final Pattern pattern = Pattern.compile("\\d+(\\.\\d+)* к.с");
      Matcher matcher = pattern.matcher(engine);

      if (matcher.find()) {
        matcher = DOUBLE_CHECK.matcher(matcher.group());
        if (matcher.find()) {
          try {
            return Double.parseDouble(matcher.group());
          } catch (NumberFormatException ignored) {
          }
        }
      }
    }

    log.debug("Unable to find horsepower from " + link);
    return null;
  }

  private Double parseKW(final String engine, final String link) {
    if (engine != null) {
      final Pattern pattern = Pattern.compile("\\d+(\\.\\d+)* кВт");
      Matcher matcher = pattern.matcher(engine);

      if (matcher.find()) {
        matcher = DOUBLE_CHECK.matcher(matcher.group());
        if (matcher.find()) {
          try {
            return Double.parseDouble(matcher.group());
          } catch (NumberFormatException ignored) {
          }
        }
      }
    }

    log.debug("Unable to find kW from " + link);
    return null;
  }

  private String parseFuel(final String engine, final String link) {
    if (engine != null) {
      final Optional<String> fuel = Arrays.stream(engine.split(DELIMITER))
          .filter(elem -> !elem.contains("к.с") && !elem.contains(" л") && !elem.contains("кВт"))
          .findFirst();
      if (fuel.isPresent()) {
        return fuel.get().trim();
      }
    }

    log.debug("Unable to find fuel from " + link);
    return null;
  }

  private Double getDistance(final List<Element> elements, final String link) {
    final Element element = getArgument(elements, "Пробіг", "distance", link);

    if (element != null) {
      final Element distance = getFirstElementByClass(element, "argument");

      if (distance != null) {
        try {
          return Double.parseDouble(
              StringUtils.substringBefore(distance.text().trim(), DELIMITER_EMPTY).trim());
        } catch (NumberFormatException ignored) {
        }
      }
    }

    log.warn("Unable to find distance from " + link);
    return null;
  }

  private Integer getOwners(final Document document, final String link) {
    final Element first = getFirstElementByClass(document, "technical-info ticket-checked");

    if (first != null) {
      final Element unstyle = getFirstElementByClass(first, "unstyle");

      if (unstyle != null) {
        final Element argument = getArgument(
            unstyle.getAllElements(),
            "Кількість власників",
            "owners",
            link
        );

        if (argument != null) {
          final Element owners = getFirstElementByClass(argument, "argument");

          if (owners != null) {
            try {
              return Integer.parseInt(owners.text().trim());
            } catch (NumberFormatException ignored) {
            }
          }
        }
      }
    }

    log.debug("Unable to find owners from " + link);
    return null;
  }

  private Integer getYear(final Document document, final String link) {
    final Element heading = getFirstElementByClass(document, "heading");

    if (heading != null) {
      final Element year = getFirstElementByClass(heading, "head");

      if (year != null) {
        try {
          return Integer.parseInt(
              StringUtils.substringAfterLast(year.text(), DELIMITER_EMPTY).trim());
        } catch (NumberFormatException ignored) {
        }
      }
    }

    log.warn("Unable to find year from " + link);
    return null;
  }

  private Integer getPrice(final Document document, final String link) {
    final Element first = getFirstElementByClass(document, "price mb-15 mhide");

    if (first != null) {
      final Element value = getFirstElementByClass(first, "price_value");

      if (value != null) {
        String price = value.getElementsByTag("strong").text();

        if (!StringUtils.endsWith(price, "$")) {
          final Element usd = first.getElementsByAttributeValue("data-currency", "USD").first();
          price = usd == null ? "" : usd.text();
        }

        try {
          return Integer.parseInt(
              StringUtils.substringBefore(price, "$")
                  .replaceAll(StringUtils.SPACE, StringUtils.EMPTY)
                  .trim()
          );
        } catch (NumberFormatException ignored) {
        }
      }
    }

    log.warn("Unable to find price from " + link);
    return null;
  }

  private String getColor(final List<Element> elements, final String link) {
    return getString(getArgument(elements, "Колір", "color", link), "color", link);
  }

  private String getCity(final Element element, int childrenSize, final String link) {
    final Element child = getChild(element, childrenSize - 3);
    if (child == null) {
      log.debug("Unable to find city from " + link);
      return null;
    }
    return child.text().trim();
  }

  private Boolean getEnabled(final Document document) {
    return document.getElementById("autoDeletedTopBlock") == null;
  }
}
