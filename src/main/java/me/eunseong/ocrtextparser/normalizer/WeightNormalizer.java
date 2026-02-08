package me.eunseong.ocrtextparser.normalizer;

import me.eunseong.ocrtextparser.domain.Weight;
import org.springframework.stereotype.Component;

/**
 * 무게 정규화
 * 단위 변환, 소수점 정리 등
 */
@Component
public class WeightNormalizer implements Normalizer<Weight> {

  private static final double EPSILON = 0.001; // 부동소수점 오차 허용

  @Override
  public Weight normalize(Weight value) {
    if (value == null) {
      return null;
    }

    Double originalValue = value.getValue();
    if (originalValue == null) {
      return value;
    }

    // 1. 음수 방지 (무게는 항상 양수)
    double normalizedValue = Math.max(0, originalValue);

    // 2. 소수점 2자리로 반올림 (kg 단위)
    normalizedValue = Math.round(normalizedValue * 100.0) / 100.0;

    // 3. 0에 가까운 값은 0으로 처리
    if (Math.abs(normalizedValue) < EPSILON) {
      normalizedValue = 0.0;
    }

    // 단위가 없으면 "kg"으로 설정
    String unit = value.getUnit();
    if (unit == null || unit.isEmpty()) {
      unit = "kg";
    }

    return Weight.builder()
        .value(normalizedValue)
        .unit(unit)
        .measuredAt(value.getMeasuredAt())
        .build();
  }

  @Override
  public Class<Weight> getSupportedType() {
    return Weight.class;
  }
}
