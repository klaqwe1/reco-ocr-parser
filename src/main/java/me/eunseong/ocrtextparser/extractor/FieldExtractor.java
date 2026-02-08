package me.eunseong.ocrtextparser.extractor;


import java.util.Optional;
import me.eunseong.ocrtextparser.domain.OcrDocument;

/**
 * OCR 문서에서 특정 필드를 추출하는 interface
 *
 * @param <T> 추출할 데이터 타입
 */
public interface FieldExtractor<T> {

  /**
   * OCR 문서에서 필드 추출
   *
   * @param document
   * @return 추출된 값
   */
  Optional<T> extract(OcrDocument document);

  default double getConfidence() {
    return 1.0;
  }

}
