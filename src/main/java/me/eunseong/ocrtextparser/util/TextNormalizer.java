package me.eunseong.ocrtextparser.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * OCR 텍스트 노이즈 제거 전담 유틸리티
 * 공백, 특수문자, 시간 패턴 등 다양한 노이즈를 제거/정규화
 */
@Component
public class TextNormalizer {

  private static final Pattern TIME_PATTERN =
      Pattern.compile("\\d{2}:\\d{2}(:\\d{2})?");

  private static final Pattern NUMBER_SEPARATOR_PATTERN =
      Pattern.compile("[,\\s]+");

  private static final Pattern SPECIAL_CHAR_PATTERN =
      Pattern.compile("[:\\-_/\\\\]");

  /**
   * 모든 노이즈 제거 (종합 정규화)
   *
   * @param text 원본 텍스트
   * @return 정규화된 텍스트
   */
  public String normalize(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }

    String result = text;
    result = removeWhitespace(result);
    result = removeSpecialChars(result);
    return result;
  }

  /**
   * 공백 제거 (모든 형태의 공백: 띄어쓰기, 탭 등)
   *
   * @param text 원본 텍스트
   * @return 공백이 제거된 텍스트
   */
  public String removeWhitespace(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }
    return text.replaceAll("\\s+", "");
  }

  /**
   * 특수문자 제거 (콜론, 하이픈, 슬래시 등)
   *
   * @param text 원본 텍스트
   * @return 특수문자가 제거된 텍스트
   */
  public String removeSpecialChars(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }
    return SPECIAL_CHAR_PATTERN.matcher(text).replaceAll("");
  }

  /**
   * 시간 패턴 제거 (HH:MM:SS 또는 HH:MM 형식)
   * 무게 추출 시 시간 값이 혼입되는 경우 대응
   *
   * @param text 원본 텍스트
   * @return 시간 패턴이 제거된 텍스트
   */
  public String removeTimePattern(String text) {
    if (text == null || text.isEmpty()) {
      return text;
    }
    return TIME_PATTERN.matcher(text).replaceAll("");
  }

  /**
   * 숫자 구분자 제거 (쉼표, 띄어쓰기)
   * "12,480" → "12480", "13 460" → "13460"
   *
   * @param numberText 숫자 텍스트
   * @return 구분자가 제거된 숫자 텍스트
   */
  public String normalizeNumber(String numberText) {
    if (numberText == null || numberText.isEmpty()) {
      return numberText;
    }
    return NUMBER_SEPARATOR_PATTERN.matcher(numberText).replaceAll("");
  }

  /**
   * 키워드 목록 정규화 (각 키워드에 정규화 적용)
   *
   * @param keywords 원본 키워드 배열
   * @return 정규화된 키워드 배열
   */
  public String[] normalizeKeywords(String[] keywords) {
    if (keywords == null || keywords.length == 0) {
      return keywords;
    }

    String[] normalized = new String[keywords.length];
    for (int i = 0; i < keywords.length; i++) {
      normalized[i] = normalize(keywords[i]);
    }
    return normalized;
  }
}
