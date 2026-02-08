package me.eunseong.ocrtextparser.extractor.strategy;

import me.eunseong.ocrtextparser.domain.OcrDocument;

import java.util.Optional;

/**
 * 필드 추출 전략 인터페이스
 * Strategy Pattern을 적용하여 다양한 추출 방식을 지원
 */
public interface ExtractionStrategy {

  /**
   * OCR 문서에서 특정 키워드에 해당하는 값을 추출
   *
   * @param document OCR 문서
   * @param keywords 찾고자 하는 키워드 배열 (예: ["차량번호", "차량 번호"])
   * @return 추출된 값 (없으면 Optional.empty())
   */
  Optional<String> extract(OcrDocument document, String[] keywords);

  /**
   * 이 전략을 사용할 수 있는지 확인
   *
   * @param document OCR 문서
   * @return 사용 가능하면 true
   */
  boolean supports(OcrDocument document);

  /**
   * 우선순위 (낮을수록 먼저 시도)
   *
   * @return 우선순위 값
   */
  int getPriority();
}
