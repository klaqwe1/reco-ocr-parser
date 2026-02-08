package me.eunseong.ocrtextparser.pipeline;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.ParsingResult;

/**
 * 파싱 파이프라인 인터페이스
 * 추출 → 정규화 → 검증 단계를 통합
 */
public interface ParsingPipeline {

  /**
   * 파싱 파이프라인 실행
   *
   * @param document OCR 문서
   * @return 파싱 결과 (성공/실패 + 데이터 + 에러/경고)
   */
  ParsingResult process(OcrDocument document);
}
