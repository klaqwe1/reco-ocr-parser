package me.eunseong.ocrtextparser.util;

import me.eunseong.ocrtextparser.config.ParserProperties;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;

/**
 * 텍스트 매칭 유틸리티
 * 정확 매칭 + Fuzzy Matching (Levenshtein Distance) 제공
 */
@Component
public class TextMatcher {

  private final TextNormalizer textNormalizer;
  private final ParserProperties parserProperties;
  private final LevenshteinDistance levenshteinDistance;

  public TextMatcher(TextNormalizer textNormalizer,
      ParserProperties parserProperties) {
    this.textNormalizer = textNormalizer;
    this.parserProperties = parserProperties;
    this.levenshteinDistance = LevenshteinDistance.getDefaultInstance();
  }

  /**
   * 텍스트에서 키워드 매칭 (정규화 + Fuzzy)
   *
   * @param text 검색 대상 텍스트
   * @param keywords 키워드 배열
   * @return 매칭 성공 시 true
   */
  public boolean matches(String text, String[] keywords) {
    if (text == null || text.isEmpty() || keywords == null ||
        keywords.length == 0) {
      return false;
    }

    // 1차: 정규화 후 정확 매칭
    String normalizedText = textNormalizer.normalize(text);
    for (String keyword : keywords) {
      String normalizedKeyword = textNormalizer.normalize(keyword);
      if (normalizedText.contains(normalizedKeyword)) {
        return true;
      }
    }

    // 2차: Fuzzy Matching (유사도 기반)
    for (String keyword : keywords) {
      if (fuzzyMatch(text, keyword)) {
        return true;
      }
    }

    return false;
  }

  /**
   * 키워드의 인덱스 찾기 (정규화된 텍스트 기준)
   *
   * @param text 검색 대상 텍스트
   * @param keywords 키워드 배열
   * @return 매칭된 키워드의 시작 인덱스 (없으면 -1)
   */
  public int findKeywordIndex(String text, String[] keywords) {
    if (text == null || text.isEmpty() || keywords == null ||
        keywords.length == 0) {
      return -1;
    }

    String normalizedText = textNormalizer.normalize(text);

    for (String keyword : keywords) {
      String normalizedKeyword = textNormalizer.normalize(keyword);
      int index = normalizedText.indexOf(normalizedKeyword);
      if (index >= 0) {
        return index;
      }
    }

    return -1;
  }

  /**
   * Fuzzy Matching (Levenshtein Distance 기반)
   * 유사도 = 1 - (거리 / 긴 문자열 길이)
   *
   * @param text 검색 대상 텍스트
   * @param keyword 키워드
   * @return 유사도가 threshold 이상이면 true
   */
  public boolean fuzzyMatch(String text, String keyword) {
    if (text == null || keyword == null) {
      return false;
    }

    String normalizedText = textNormalizer.normalize(text);
    String normalizedKeyword = textNormalizer.normalize(keyword);

    // 슬라이딩 윈도우 방식: 키워드 길이만큼 잘라서 비교
    int keywordLen = normalizedKeyword.length();
    int textLen = normalizedText.length();

    if (keywordLen > textLen) {
      return false;
    }

    double threshold = parserProperties.getFuzzyMatchThreshold();

    for (int i = 0; i <= textLen - keywordLen; i++) {
      String window = normalizedText.substring(i, i + keywordLen);
      double similarity = calculateSimilarity(window, normalizedKeyword);

      if (similarity >= threshold) {
        return true;
      }
    }

    return false;
  }

  /**
   * 유사도 계산
   * similarity = 1 - (distance / maxLength)
   *
   * @param str1 문자열 1
   * @param str2 문자열 2
   * @return 유사도 (0.0 ~ 1.0)
   */
  private double calculateSimilarity(String str1, String str2) {
    if (str1 == null || str2 == null) {
      return 0.0;
    }

    if (str1.equals(str2)) {
      return 1.0;
    }

    int distance = levenshteinDistance.apply(str1, str2);
    int maxLength = Math.max(str1.length(), str2.length());

    if (maxLength == 0) {
      return 1.0;
    }

    return 1.0 - ((double) distance / maxLength);
  }
}
