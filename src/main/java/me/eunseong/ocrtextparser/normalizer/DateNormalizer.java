package me.eunseong.ocrtextparser.normalizer;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 날짜 정규화
 * 현재는 LocalDate가 이미 표준 형식이므로 추가 작업 없음
 * 향후 시간대 변환, 유효성 검증 등 추가 가능
 */
@Component
public class DateNormalizer implements Normalizer<LocalDate> {

  @Override
  public LocalDate normalize(LocalDate value) {
    if (value == null) {
      return null;
    }

    // 현재는 그대로 반환
    // 향후 확장: 날짜 범위 검증, 시간대 변환 등
    return value;
  }

  @Override
  public Class<LocalDate> getSupportedType() {
    return LocalDate.class;
  }
}
