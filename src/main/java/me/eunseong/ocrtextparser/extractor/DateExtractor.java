package me.eunseong.ocrtextparser.extractor;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.extractor.strategy.ExtractionStrategy;
import me.eunseong.ocrtextparser.util.TextNormalizer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 날짜 추출기
 * 키워드: "계량일자", "날짜" 등
 * Fallback: 키워드 없이 날짜 패턴만 탐색
 */
@Component
public class DateExtractor extends AbstractFieldExtractor<LocalDate> {

  // YYYY-MM-DD 또는 YYYY.MM.DD 패턴
  private static final Pattern DATE_PATTERN =
      Pattern.compile("(\\d{4})[-.]?(\\d{2})[-.]?(\\d{2})");

  public DateExtractor(List<ExtractionStrategy> strategies,
      TextNormalizer textNormalizer) {
    super(strategies, textNormalizer);
  }

  @Override
  protected String[] getKeywords() {
    return new String[]{"계량일자", "날짜", "일시"};
  }

  @Override
  protected Optional<LocalDate> postProcess(String rawValue,
      OcrDocument document) {
    return parseDateFromString(rawValue);
  }

  @Override
  protected Optional<LocalDate> extractWithFallback(OcrDocument document) {
    // 키워드 없이 날짜 패턴만 찾기
    if (document.getLines() == null) {
      return Optional.empty();
    }

    for (String line : document.getLines()) {
      Optional<LocalDate> date = parseDateFromString(line);
      if (date.isPresent()) {
        return date;
      }
    }

    return Optional.empty();
  }

  /**
   * 문자열에서 날짜 파싱
   *
   * @param text 날짜가 포함된 문자열
   * @return 파싱된 날짜
   */
  private Optional<LocalDate> parseDateFromString(String text) {
    if (text == null || text.isEmpty()) {
      return Optional.empty();
    }

    Matcher matcher = DATE_PATTERN.matcher(text);
    if (matcher.find()) {
      try {
        int year = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int day = Integer.parseInt(matcher.group(3));

        return Optional.of(LocalDate.of(year, month, day));
      } catch (Exception e) {
        // 파싱 실패 (잘못된 날짜 값)
      }
    }

    return Optional.empty();
  }
}
