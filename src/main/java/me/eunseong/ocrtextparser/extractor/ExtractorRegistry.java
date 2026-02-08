package me.eunseong.ocrtextparser.extractor;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 필드 추출기 레지스트리
 * 모든 Extractor를 중앙에서 관리하고 필드명으로 조회
 */
@Component
public class ExtractorRegistry {

  private final Map<String, FieldExtractor<?>> extractors;

  public ExtractorRegistry(
      DateExtractor dateExtractor,
      VehicleNumberExtractor vehicleNumberExtractor,
      WeightExtractor weightExtractor,
      CompanyExtractor companyExtractor
  ) {
    this.extractors = Map.of(
        "date", dateExtractor,
        "vehicleNumber", vehicleNumberExtractor,
        "weight", weightExtractor,
        "company", companyExtractor
    );
  }

  /**
   * 필드명으로 Extractor 조회
   *
   * @param fieldName 필드명
   * @return Extractor (없으면 Optional.empty())
   */
  public Optional<FieldExtractor<?>> getExtractor(String fieldName) {
    return Optional.ofNullable(extractors.get(fieldName));
  }

  /**
   * 모든 Extractor 반환 (읽기 전용)
   *
   * @return 모든 Extractor의 Map
   */
  public Map<String, FieldExtractor<?>> getAllExtractors() {
    return Collections.unmodifiableMap(extractors);
  }

  /**
   * 등록된 Extractor 개수 반환
   *
   * @return Extractor 개수
   */
  public int size() {
    return extractors.size();
  }

  /**
   * 특정 필드명이 등록되어 있는지 확인
   *
   * @param fieldName 필드명
   * @return 등록 여부
   */
  public boolean contains(String fieldName) {
    return extractors.containsKey(fieldName);
  }
}
