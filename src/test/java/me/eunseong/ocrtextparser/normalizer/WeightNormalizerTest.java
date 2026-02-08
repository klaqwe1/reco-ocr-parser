package me.eunseong.ocrtextparser.normalizer;

import me.eunseong.ocrtextparser.domain.Weight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class WeightNormalizerTest {

  private WeightNormalizer normalizer;

  @BeforeEach
  void setUp() {
    normalizer = new WeightNormalizer();
  }

  @Test
  @DisplayName("정상적인 무게 값 정규화")
  void normalizeValidWeight() {
    // given
    Weight weight = Weight.builder()
        .value(12345.678)
        .unit("kg")
        .build();

    // when
    Weight normalized = normalizer.normalize(weight);

    // then
    assertThat(normalized.getValue()).isEqualTo(12345.68); // 소수점 2자리 반올림
    assertThat(normalized.getUnit()).isEqualTo("kg");
  }

  @Test
  @DisplayName("음수 무게는 0으로 정규화")
  void normalizeNegativeWeight() {
    // given
    Weight weight = Weight.builder()
        .value(-100.0)
        .unit("kg")
        .build();

    // when
    Weight normalized = normalizer.normalize(weight);

    // then
    assertThat(normalized.getValue()).isEqualTo(0.0);
  }

  @Test
  @DisplayName("0에 가까운 값은 0으로 정규화")
  void normalizeNearZeroWeight() {
    // given
    Weight weight = Weight.builder()
        .value(0.0001)
        .unit("kg")
        .build();

    // when
    Weight normalized = normalizer.normalize(weight);

    // then
    assertThat(normalized.getValue()).isEqualTo(0.0);
  }

  @Test
  @DisplayName("단위가 없으면 kg으로 설정")
  void normalizeWithoutUnit() {
    // given
    Weight weight = Weight.builder()
        .value(1000.0)
        .build();

    // when
    Weight normalized = normalizer.normalize(weight);

    // then
    assertThat(normalized.getUnit()).isEqualTo("kg");
  }

  @Test
  @DisplayName("측정 시간은 유지")
  void normalizeMaintainsMeasuredAt() {
    // given
    LocalDateTime now = LocalDateTime.now();
    Weight weight = Weight.builder()
        .value(1234.567)
        .unit("kg")
        .measuredAt(now)
        .build();

    // when
    Weight normalized = normalizer.normalize(weight);

    // then
    assertThat(normalized.getMeasuredAt()).isEqualTo(now);
  }

  @Test
  @DisplayName("null 값 처리")
  void normalizeNullWeight() {
    // when
    Weight normalized = normalizer.normalize(null);

    // then
    assertThat(normalized).isNull();
  }

  @Test
  @DisplayName("value가 null인 경우")
  void normalizeNullValue() {
    // given
    Weight weight = Weight.builder()
        .unit("kg")
        .build();

    // when
    Weight normalized = normalizer.normalize(weight);

    // then
    assertThat(normalized).isNotNull();
    assertThat(normalized.getValue()).isNull();
  }
}
