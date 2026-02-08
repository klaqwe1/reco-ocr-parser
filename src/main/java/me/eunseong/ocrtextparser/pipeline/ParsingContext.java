package me.eunseong.ocrtextparser.pipeline;

import lombok.Builder;
import lombok.Getter;
import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.WeighingSlip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 파싱 컨텍스트
 * 파싱 과정에서 상태를 공유하고 에러/경고를 수집
 */
@Getter
@Builder
public class ParsingContext {

  private final OcrDocument document;
  private final WeighingSlip.WeighingSlipBuilder slipBuilder;

  @Builder.Default
  private final List<String> errors = new ArrayList<>();

  @Builder.Default
  private final List<String> warnings = new ArrayList<>();

  @Builder.Default
  private final Map<String, Object> metadata = new HashMap<>();

  /**
   * 에러 추가
   */
  public void addError(String error) {
    if (error != null && !error.isEmpty()) {
      errors.add(error);
    }
  }

  /**
   * 경고 추가
   */
  public void addWarning(String warning) {
    if (warning != null && !warning.isEmpty()) {
      warnings.add(warning);
    }
  }

  /**
   * 메타데이터 추가
   */
  public void putMetadata(String key, Object value) {
    metadata.put(key, value);
  }

  /**
   * 메타데이터 조회
   */
  public Object getMetadata(String key) {
    return metadata.get(key);
  }

  /**
   * 에러가 있는지 확인
   */
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  /**
   * 경고가 있는지 확인
   */
  public boolean hasWarnings() {
    return !warnings.isEmpty();
  }
}
