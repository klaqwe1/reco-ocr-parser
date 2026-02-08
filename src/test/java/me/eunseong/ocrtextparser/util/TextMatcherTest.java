package me.eunseong.ocrtextparser.util;

import me.eunseong.ocrtextparser.config.ParserProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextMatcherTest {

  private TextMatcher textMatcher;
  private TextNormalizer textNormalizer;
  private ParserProperties parserProperties;

  @BeforeEach
  void setUp() {
    textNormalizer = new TextNormalizer();
    parserProperties = new ParserProperties();
    parserProperties.setFuzzyMatchThreshold(0.75);
    textMatcher = new TextMatcher(textNormalizer, parserProperties);
  }

  @Test
  @DisplayName("정확 매칭 - 공백 정규화")
  void exactMatchWithWhitespace() {
    // given
    String text = "차 량 번 호 : 1234";
    String[] keywords = {"차량번호"};

    // when
    boolean result = textMatcher.matches(text, keywords);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("정확 매칭 - 특수문자 포함")
  void exactMatchWithSpecialChars() {
    // given
    String text = "차량-번호: 1234";
    String[] keywords = {"차량번호"};

    // when
    boolean result = textMatcher.matches(text, keywords);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("Fuzzy 매칭 - 오탈자")
  void fuzzyMatchWithTypo() {
    // given
    String text = "차랑번호: 1234";  // '량' → '랑' 오탈자
    String[] keywords = {"차량번호"};

    // when
    boolean result = textMatcher.matches(text, keywords);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("복수 키워드 매칭")
  void multipleKeywords() {
    // given
    String text = "차번호: 1234";
    String[] keywords = {"차량번호", "차량 번호", "차번호"};

    // when
    boolean result = textMatcher.matches(text, keywords);

    // then
    assertThat(result).isTrue();
  }

  @Test
  @DisplayName("키워드 인덱스 찾기")
  void findKeywordIndex() {
    // given
    String text = "차 량 번 호 : 1234";
    String[] keywords = {"차량번호"};

    // when
    int index = textMatcher.findKeywordIndex(text, keywords);

    // then
    assertThat(index).isGreaterThanOrEqualTo(0);
  }

  @Test
  @DisplayName("매칭 실패 - 전혀 다른 텍스트")
  void noMatch() {
    // given
    String text = "총중량: 12480kg";
    String[] keywords = {"차량번호"};

    // when
    boolean result = textMatcher.matches(text, keywords);

    // then
    assertThat(result).isFalse();
  }

  @Test
  @DisplayName("null 또는 빈 값 처리")
  void handleNullOrEmpty() {
    // given
    String[] keywords = {"차량번호"};

    // when & then
    assertThat(textMatcher.matches(null, keywords)).isFalse();
    assertThat(textMatcher.matches("", keywords)).isFalse();
    assertThat(textMatcher.matches("차량번호", null)).isFalse();
    assertThat(textMatcher.matches("차량번호", new String[]{})).isFalse();
  }
}
