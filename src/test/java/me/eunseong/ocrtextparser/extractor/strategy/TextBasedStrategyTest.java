package me.eunseong.ocrtextparser.extractor.strategy;

import me.eunseong.ocrtextparser.config.ParserProperties;
import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.util.TextMatcher;
import me.eunseong.ocrtextparser.util.TextNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TextBasedStrategyTest {

  private TextBasedStrategy strategy;
  private TextMatcher textMatcher;
  private TextNormalizer textNormalizer;

  @BeforeEach
  void setUp() {
    textNormalizer = new TextNormalizer();
    ParserProperties parserProperties = new ParserProperties();
    parserProperties.setFuzzyMatchThreshold(0.75);
    textMatcher = new TextMatcher(textNormalizer, parserProperties);
    strategy = new TextBasedStrategy(textMatcher, textNormalizer);
  }

  @Test
  @DisplayName("차량번호 추출 성공")
  void extractVehicleNumber() {
    // given
    List<String> lines = Arrays.asList(
        "계근표",
        "차량번호: 8713",
        "총중량: 12480kg"
    );
    OcrDocument document = OcrDocument.builder()
        .lines(lines)
        .build();
    String[] keywords = {"차량번호"};

    // when
    Optional<String> result = strategy.extract(document, keywords);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).contains("8713");
  }

  @Test
  @DisplayName("공백이 포함된 키워드 추출")
  void extractWithWhitespace() {
    // given
    List<String> lines = Arrays.asList(
        "차 량 번 호 : 1234"
    );
    OcrDocument document = OcrDocument.builder()
        .lines(lines)
        .build();
    String[] keywords = {"차량번호", "차량 번호"};

    // when
    Optional<String> result = strategy.extract(document, keywords);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).contains("1234");
  }

  @Test
  @DisplayName("오탈자가 있는 키워드 추출 (Fuzzy Matching)")
  void extractWithTypo() {
    // given
    List<String> lines = Arrays.asList(
        "차랑번호: 5678"  // '량' → '랑' 오탈자
    );
    OcrDocument document = OcrDocument.builder()
        .lines(lines)
        .build();
    String[] keywords = {"차량번호"};

    // when
    Optional<String> result = strategy.extract(document, keywords);

    // then
    assertThat(result).isPresent();
    assertThat(result.get()).contains("5678");
  }

  @Test
  @DisplayName("키워드가 없으면 추출 실패")
  void noKeywordFound() {
    // given
    List<String> lines = Arrays.asList(
        "총중량: 12480kg",
        "차중량: 7470kg"
    );
    OcrDocument document = OcrDocument.builder()
        .lines(lines)
        .build();
    String[] keywords = {"차량번호"};

    // when
    Optional<String> result = strategy.extract(document, keywords);

    // then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("supports 확인 - 라인 정보 있음")
  void supportsWithLines() {
    // given
    OcrDocument document = OcrDocument.builder()
        .lines(Arrays.asList("line1", "line2"))
        .build();

    // when & then
    assertThat(strategy.supports(document)).isTrue();
  }

  @Test
  @DisplayName("supports 확인 - 라인 정보 없음")
  void supportsWithoutLines() {
    // given
    OcrDocument document = OcrDocument.builder().build();

    // when & then
    assertThat(strategy.supports(document)).isFalse();
  }

  @Test
  @DisplayName("우선순위 확인")
  void checkPriority() {
    // when & then
    assertThat(strategy.getPriority()).isEqualTo(1);
  }
}
