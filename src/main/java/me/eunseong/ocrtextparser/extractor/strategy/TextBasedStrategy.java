package me.eunseong.ocrtextparser.extractor.strategy;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.util.TextMatcher;
import me.eunseong.ocrtextparser.util.TextNormalizer;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 텍스트 기반 추출 전략
 * 라인별로 키워드를 찾고, 키워드 이후의 값을 추출
 * 공백 정규화 + Fuzzy Matching 지원
 */
@Component
public class TextBasedStrategy implements ExtractionStrategy {

  private final TextMatcher textMatcher;
  private final TextNormalizer textNormalizer;

  // 값 추출 패턴: 영문, 숫자, 한글, 일부 특수문자 조합
  private static final Pattern VALUE_PATTERN =
      Pattern.compile("[가-힣a-zA-Z0-9,\\s.\\-_]+");

  public TextBasedStrategy(TextMatcher textMatcher,
      TextNormalizer textNormalizer) {
    this.textMatcher = textMatcher;
    this.textNormalizer = textNormalizer;
  }

  @Override
  public Optional<String> extract(OcrDocument document, String[] keywords) {
    if (document == null || document.getLines() == null ||
        keywords == null || keywords.length == 0) {
      return Optional.empty();
    }

    for (String line : document.getLines()) {
      if (textMatcher.matches(line, keywords)) {
        Optional<String> value = extractValueFromLine(line, keywords);
        if (value.isPresent()) {
          return value;
        }
      }
    }

    return Optional.empty();
  }

  @Override
  public boolean supports(OcrDocument document) {
    // 라인 정보가 있으면 사용 가능
    return document != null && document.getLines() != null &&
        !document.getLines().isEmpty();
  }

  @Override
  public int getPriority() {
    // 우선순위 1 (텍스트 기반이 먼저 시도)
    return 1;
  }

  /**
   * 라인에서 키워드 이후의 값 추출
   *
   * @param line 라인 텍스트
   * @param keywords 키워드 배열
   * @return 추출된 값
   */
  private Optional<String> extractValueFromLine(String line,
      String[] keywords) {
    // 키워드 위치 찾기
    int keywordIndex = textMatcher.findKeywordIndex(line, keywords);
    if (keywordIndex < 0) {
      return Optional.empty();
    }

    // 키워드 이후 텍스트 추출
    String normalizedLine = textNormalizer.normalize(line);

    // 키워드 길이 계산 (정규화된 키워드 중 가장 짧은 것 기준)
    int minKeywordLen = Integer.MAX_VALUE;
    for (String keyword : keywords) {
      int len = textNormalizer.normalize(keyword).length();
      if (len < minKeywordLen) {
        minKeywordLen = len;
      }
    }

    int valueStartIndex = keywordIndex + minKeywordLen;
    if (valueStartIndex >= normalizedLine.length()) {
      return Optional.empty();
    }

    String afterKeyword = normalizedLine.substring(valueStartIndex);

    // 원본 라인에서도 값 추출 시도 (공백 유지)
    String originalAfterKeyword = extractAfterKeywordFromOriginal(line,
        keywords);
    if (originalAfterKeyword != null) {
      return extractValue(originalAfterKeyword);
    }

    return extractValue(afterKeyword);
  }

  /**
   * 원본 라인에서 키워드 이후 텍스트 추출
   *
   * @param line 원본 라인
   * @param keywords 키워드 배열
   * @return 키워드 이후 텍스트 (없으면 null)
   */
  private String extractAfterKeywordFromOriginal(String line,
      String[] keywords) {
    for (String keyword : keywords) {
      int index = line.indexOf(keyword);
      if (index >= 0) {
        int afterIndex = index + keyword.length();
        if (afterIndex < line.length()) {
          return line.substring(afterIndex);
        }
      }
    }
    return null;
  }

  /**
   * 텍스트에서 값 추출 (콜론 제거, 패턴 매칭)
   *
   * @param text 텍스트
   * @return 추출된 값
   */
  private Optional<String> extractValue(String text) {
    if (text == null || text.isEmpty()) {
      return Optional.empty();
    }

    // 콜론 제거
    text = text.replace(":", "").trim();

    // 패턴 매칭
    Matcher matcher = VALUE_PATTERN.matcher(text);
    if (matcher.find()) {
      String value = matcher.group().trim();
      if (!value.isEmpty()) {
        return Optional.of(value);
      }
    }

    return Optional.empty();
  }
}
