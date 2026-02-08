package me.eunseong.ocrtextparser.validator;

import me.eunseong.ocrtextparser.config.ParserProperties;
import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.domain.WeighingSlip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessRuleValidatorTest {

  private BusinessRuleValidator validator;
  private ParserProperties properties;

  @BeforeEach
  void setUp() {
    properties = new ParserProperties();
    properties.getValidation().setWeightTolerance(10.0);
    validator = new BusinessRuleValidator(properties);
  }

  @Test
  @DisplayName("정확한 무게 계산 - 검증 통과")
  void validateCorrectWeightCalculation() {
    // given
    WeighingSlip slip = WeighingSlip.builder()
        .date(LocalDate.now())
        .vehicleNumber("1234")
        .totalWeight(Weight.builder().value(12480.0).unit("kg").build())
        .vehicleWeight(Weight.builder().value(7470.0).unit("kg").build())
        .netWeight(Weight.builder().value(5010.0).unit("kg").build())
        .build();

    // when
    List<String> errors = validator.validate(slip);

    // then
    assertThat(errors).isEmpty();
  }

  @Test
  @DisplayName("허용 오차 범위 내 - 검증 통과")
  void validateWithinTolerance() {
    // given
    WeighingSlip slip = WeighingSlip.builder()
        .date(LocalDate.now())
        .vehicleNumber("1234")
        .totalWeight(Weight.builder().value(12480.0).unit("kg").build())
        .vehicleWeight(Weight.builder().value(7470.0).unit("kg").build())
        .netWeight(Weight.builder().value(5015.0).unit("kg").build()) // 5kg 차이 (허용 범위 내)
        .build();

    // when
    List<String> errors = validator.validate(slip);

    // then
    assertThat(errors).isEmpty();
  }

  @Test
  @DisplayName("허용 오차 초과 - 검증 실패")
  void validateExceedTolerance() {
    // given
    WeighingSlip slip = WeighingSlip.builder()
        .date(LocalDate.now())
        .vehicleNumber("1234")
        .totalWeight(Weight.builder().value(12480.0).unit("kg").build())
        .vehicleWeight(Weight.builder().value(7470.0).unit("kg").build())
        .netWeight(Weight.builder().value(6000.0).unit("kg").build()) // 990kg 차이 (허용 범위 초과)
        .build();

    // when
    List<String> errors = validator.validate(slip);

    // then
    assertThat(errors).hasSize(1);
    assertThat(errors.get(0)).contains("무게 계산이 맞지 않습니다");
  }

  @Test
  @DisplayName("총중량 < 차중량 - 검증 실패")
  void validateTotalLessThanVehicle() {
    // given
    WeighingSlip slip = WeighingSlip.builder()
        .date(LocalDate.now())
        .vehicleNumber("1234")
        .totalWeight(Weight.builder().value(5000.0).unit("kg").build())
        .vehicleWeight(Weight.builder().value(10000.0).unit("kg").build())
        .netWeight(Weight.builder().value(-5000.0).unit("kg").build())
        .build();

    // when
    List<String> errors = validator.validate(slip);

    // then - 여러 에러가 발생할 수 있음 (무게 계산 오류, 총중량<차중량, 실중량 음수)
    assertThat(errors).isNotEmpty();
    assertThat(errors).anyMatch(e -> e.contains("총중량") && e.contains("보다 작습니다"));
  }

  @Test
  @DisplayName("실중량 < 0 - 검증 실패")
  void validateNegativeNetWeight() {
    // given
    WeighingSlip slip = WeighingSlip.builder()
        .date(LocalDate.now())
        .vehicleNumber("1234")
        .totalWeight(Weight.builder().value(10000.0).unit("kg").build())
        .vehicleWeight(Weight.builder().value(5000.0).unit("kg").build())
        .netWeight(Weight.builder().value(-100.0).unit("kg").build())
        .build();

    // when
    List<String> errors = validator.validate(slip);

    // then - 여러 에러가 발생할 수 있음
    assertThat(errors).isNotEmpty();
    assertThat(errors).anyMatch(e -> e.contains("실중량") && e.contains("음수"));
  }

  @Test
  @DisplayName("무게 필드 누락 시 검증 스킵")
  void validateMissingFields() {
    // given
    WeighingSlip slip = WeighingSlip.builder()
        .date(LocalDate.now())
        .vehicleNumber("1234")
        .build();

    // when
    List<String> errors = validator.validate(slip);

    // then
    assertThat(errors).isEmpty(); // RequiredFieldValidator가 처리
  }

  @Test
  @DisplayName("null 처리")
  void validateNullSlip() {
    // when
    List<String> errors = validator.validate(null);

    // then
    assertThat(errors).isEmpty();
  }

  @Test
  @DisplayName("검증 순서 확인")
  void checkOrder() {
    // when
    int order = validator.getOrder();

    // then
    assertThat(order).isEqualTo(2); // 필수 필드 검증 이후
  }
}
