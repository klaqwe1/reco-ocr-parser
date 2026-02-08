package me.eunseong.ocrtextparser.extractor.weight;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.extractor.strategy.ExtractionStrategy;
import me.eunseong.ocrtextparser.util.TextNormalizer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 총중량 추출기
 * 키워드가 없으면 fallback으로 첫 번째 kg 값 사용
 */
@Component
public class TotalWeightExtractor extends AbstractWeightFieldExtractor {

  private static final Pattern WEIGHT_PATTERN =
      Pattern.compile("([\\d,\\s]+)\\s*kg", Pattern.CASE_INSENSITIVE);
  private static final Pattern TIME_PATTERN =
      Pattern.compile("(\\d{2}):(\\d{2})(?::(\\d{2}))?");
  private static final Pattern DATE_PATTERN =
      Pattern.compile("(\\d{4})[-.]?(\\d{2})[-.]?(\\d{2})");

  public TotalWeightExtractor(List<ExtractionStrategy> strategies,
      TextNormalizer textNormalizer) {
    super(strategies, textNormalizer);
  }

  @Override
  protected String[] getKeywords() {
    return new String[]{"총중량", "총 중량"};
  }

  @Override
  protected Optional<Weight> extractWithFallback(OcrDocument document) {
    // Fallback: 문서에서 첫 번째 kg 값 추출
    if (document.getLines() == null) {
      return Optional.empty();
    }

    LocalDate baseDate = extractDateFromDocument(document);
    List<Weight> allWeights = new ArrayList<>();

    for (String line : document.getLines()) {
      // 시간 추출
      LocalTime time = extractTime(line).orElse(null);
      LocalDateTime measuredAt = null;
      if (time != null && baseDate != null) {
        measuredAt = LocalDateTime.of(baseDate, time);
      }

      // 시간 패턴 제거
      String cleanedLine = textNormalizer.removeTimePattern(line);

      // 무게 값 추출
      Matcher matcher = WEIGHT_PATTERN.matcher(cleanedLine);
      if (matcher.find()) {
        String numberStr = matcher.group(1);
        numberStr = textNormalizer.normalizeNumber(numberStr);

        try {
          double value = Double.parseDouble(numberStr);
          allWeights.add(Weight.builder()
              .value(value)
              .unit("kg")
              .measuredAt(measuredAt)
              .build());
        } catch (NumberFormatException e) {
          // ignore
        }
      }
    }

    // 첫 번째 무게 반환 (총중량으로 간주)
    return allWeights.isEmpty() ? Optional.empty() :
        Optional.of(allWeights.get(0));
  }

  private LocalDate extractDateFromDocument(OcrDocument document) {
    if (document.getLines() == null) {
      return null;
    }

    for (String line : document.getLines()) {
      Matcher matcher = DATE_PATTERN.matcher(line);
      if (matcher.find()) {
        try {
          return LocalDate.of(
              Integer.parseInt(matcher.group(1)),
              Integer.parseInt(matcher.group(2)),
              Integer.parseInt(matcher.group(3))
          );
        } catch (Exception e) {
          // ignore
        }
      }
    }
    return null;
  }

  private Optional<LocalTime> extractTime(String text) {
    if (text == null || text.isEmpty()) {
      return Optional.empty();
    }

    Matcher matcher = TIME_PATTERN.matcher(text);
    if (matcher.find()) {
      try {
        String hour = matcher.group(1);
        String minute = matcher.group(2);
        String second = matcher.group(3);

        if (second != null) {
          return Optional.of(LocalTime.parse(
              hour + ":" + minute + ":" + second,
              DateTimeFormatter.ofPattern("HH:mm:ss")
          ));
        } else {
          return Optional.of(LocalTime.parse(
              hour + ":" + minute,
              DateTimeFormatter.ofPattern("HH:mm")
          ));
        }
      } catch (Exception e) {
        // 파싱 실패
      }
    }
    return Optional.empty();
  }
}
