package me.eunseong.ocrtextparser.extractor.weight;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.extractor.AbstractFieldExtractor;
import me.eunseong.ocrtextparser.extractor.strategy.ExtractionStrategy;
import me.eunseong.ocrtextparser.util.TextNormalizer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 무게 추출기의 추상 클래스
 * 총중량, 차중량, 실중량 추출기가 상속
 */
public abstract class AbstractWeightFieldExtractor extends
    AbstractFieldExtractor<Weight> {

  // 시간 패턴: HH:MM:SS 또는 HH:MM
  private static final Pattern TIME_PATTERN =
      Pattern.compile("(\\d{2}):(\\d{2})(?::(\\d{2}))?");

  // 숫자 패턴: 쉼표/띄어쓰기 포함된 숫자 + kg
  private static final Pattern WEIGHT_PATTERN =
      Pattern.compile("([\\d,\\s]+)\\s*kg", Pattern.CASE_INSENSITIVE);

  // 날짜 패턴
  private static final Pattern DATE_PATTERN =
      Pattern.compile("(\\d{4})[-.]?(\\d{2})[-.]?(\\d{2})");

  protected AbstractWeightFieldExtractor(List<ExtractionStrategy> strategies,
      TextNormalizer textNormalizer) {
    super(strategies, textNormalizer);
  }

  @Override
  protected Optional<Weight> postProcess(String rawValue,
      OcrDocument document) {
    // 1. 날짜 찾기
    LocalDate baseDate = extractDateFromDocument(document);

    // 2. 시간 추출
    LocalTime time = extractTime(rawValue).orElse(null);

    // 3. LocalDateTime 생성
    LocalDateTime measuredAt = null;
    if (time != null && baseDate != null) {
      measuredAt = LocalDateTime.of(baseDate, time);
    }

    // 4. 시간 패턴 제거 (무게 값만 추출하기 위해)
    String cleanedValue = textNormalizer.removeTimePattern(rawValue);

    // 5. 무게 값 추출
    Matcher matcher = WEIGHT_PATTERN.matcher(cleanedValue);
    if (matcher.find()) {
      String numberStr = matcher.group(1);
      // 쉼표, 띄어쓰기 제거
      numberStr = textNormalizer.normalizeNumber(numberStr);

      try {
        double value = Double.parseDouble(numberStr);
        return Optional.of(Weight.builder()
            .value(value)
            .unit("kg")
            .measuredAt(measuredAt)
            .build());
      } catch (NumberFormatException e) {
        // 파싱 실패
      }
    }

    return Optional.empty();
  }

  /**
   * 문서에서 날짜 추출
   */
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

  /**
   * 시간 추출
   */
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
