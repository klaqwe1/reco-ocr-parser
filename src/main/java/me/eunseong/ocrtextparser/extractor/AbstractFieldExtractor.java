package me.eunseong.ocrtextparser.extractor;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.extractor.strategy.ExtractionStrategy;
import me.eunseong.ocrtextparser.util.TextNormalizer;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 필드 추출기의 추상 클래스 (Template Method Pattern)
 * 공통 추출 로직을 제공하고, 구체 클래스는 키워드와 후처리만 구현
 *
 * @param <T> 추출할 데이터 타입
 */
public abstract class AbstractFieldExtractor<T> implements FieldExtractor<T> {

  private final List<ExtractionStrategy> strategies;
  protected final TextNormalizer textNormalizer;

  protected AbstractFieldExtractor(List<ExtractionStrategy> strategies,
      TextNormalizer textNormalizer) {
    this.strategies = strategies.stream()
        .sorted(Comparator.comparingInt(ExtractionStrategy::getPriority))
        .toList();
    this.textNormalizer = textNormalizer;
  }

  @Override
  public Optional<T> extract(OcrDocument document) {
    if (document == null) {
      return Optional.empty();
    }

    // 1. 키워드 가져오기
    String[] keywords = getKeywords();

    // 2. 전략 순회 (우선순위 순서: TextBased → PositionBased)
    for (ExtractionStrategy strategy : strategies) {
      if (strategy.supports(document)) {
        Optional<String> rawValue = strategy.extract(document, keywords);
        if (rawValue.isPresent()) {
          // 3. 후처리 (구체 클래스에서 구현)
          Optional<T> processed = postProcess(rawValue.get(), document);
          if (processed.isPresent()) {
            return processed;
          }
        }
      }
    }

    // 4. Fallback 로직 (선택적, 구체 클래스에서 오버라이드)
    return extractWithFallback(document);
  }

  /**
   * 추출에 사용할 키워드 배열 반환
   * 구체 클래스에서 구현
   *
   * @return 키워드 배열
   */
  protected abstract String[] getKeywords();

  /**
   * 추출된 원본 문자열을 타입 T로 변환
   * 구체 클래스에서 구현
   *
   * @param rawValue 추출된 원본 문자열
   * @param document 원본 OCR 문서 (필요 시 사용)
   * @return 변환된 값
   */
  protected abstract Optional<T> postProcess(String rawValue,
      OcrDocument document);

  /**
   * Fallback 추출 로직 (선택적)
   * 전략 기반 추출이 모두 실패했을 때 호출
   * 기본 구현은 empty 반환
   *
   * @param document OCR 문서
   * @return 추출된 값
   */
  protected Optional<T> extractWithFallback(OcrDocument document) {
    return Optional.empty();
  }

  /**
   * 전략 목록을 반환 (서브클래스에서 재사용 가능)
   *
   * @return 전략 목록
   */
  protected List<ExtractionStrategy> getStrategies() {
    return strategies;
  }
}
