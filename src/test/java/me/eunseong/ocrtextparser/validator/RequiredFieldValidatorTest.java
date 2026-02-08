package me.eunseong.ocrtextparser.validator;

import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.domain.WeighingSlip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequiredFieldValidatorTest {

  private RequiredFieldValidator validator;

  @BeforeEach
  void setUp() {
    validator = new RequiredFieldValidator();
  }

  @Test
  @DisplayName("모든 필수 필드가 있으면 검증 통과")
  void validateAllFieldsPresent() {
    // given
    WeighingSlip slip = WeighingSlip.builder()
        .date(LocalDate.now())
        .vehicleNumber("1234")
        .totalWeight(Weight.builder().value(10000.0).unit("kg").build())
        .vehicleWeight(Weight.builder().value(5000.0).unit("kg").build())
        .netWeight(Weight.builder().value(5000.0).unit("kg").build())
        .build();

    // when
    List<String> errors = validator.validate(slip);

    // then
    assertThat(errors).isEmpty();
  }

  @Test
  @DisplayName("날짜 누락 시 에러")
  void validateMissingDate() {
    // given
    WeighingSlip slip = WeighingSlip.builder()
        .vehicleNumber("1234")
        .totalWeight(Weight.builder().value(10000.0).unit("kg").build())
        .vehicleWeight(Weight.builder().value(5000.0).unit("kg").build())
        .netWeight(Weight.builder().value(5000.0).unit("kg").build())
        .build();

    // when
    List<String> errors = validator.validate(slip);

    // then
    assertThat(errors).hasSize(1);
    assertThat(errors.get(0)).contains("날짜가 누락");
  }

  @Test
  @DisplayName("차량번호 누락 시 에러")
  void validateMissingVehicleNumber() {
    // given
    WeighingSlip slip = WeighingSlip.builder()
        .date(LocalDate.now())
        .totalWeight(Weight.builder().value(10000.0).unit("kg").build())
        .vehicleWeight(Weight.builder().value(5000.0).unit("kg").build())
        .netWeight(Weight.builder().value(5000.0).unit("kg").build())
        .build();

    // when
    List<String> errors = validator.validate(slip);

    // then
    assertThat(errors).hasSize(1);
    assertThat(errors.get(0)).contains("차량번호가 누락");
  }

  @Test
  @DisplayName("무게 필드 누락 시 에러")
  void validateMissingWeights() {
    // given
    WeighingSlip slip = WeighingSlip.builder()
        .date(LocalDate.now())
        .vehicleNumber("1234")
        .build();

    // when
    List<String> errors = validator.validate(slip);

    // then
    assertThat(errors).hasSize(3);
    assertThat(errors).anyMatch(e -> e.contains("총중량"));
    assertThat(errors).anyMatch(e -> e.contains("차중량"));
    assertThat(errors).anyMatch(e -> e.contains("실중량"));
  }

  @Test
  @DisplayName("null 처리")
  void validateNullSlip() {
    // when
    List<String> errors = validator.validate(null);

    // then
    assertThat(errors).hasSize(1);
    assertThat(errors.get(0)).contains("null");
  }

  @Test
  @DisplayName("검증 순서 확인")
  void checkOrder() {
    // when
    int order = validator.getOrder();

    // then
    assertThat(order).isEqualTo(1); // 필수 필드 검증이 먼저
  }
}
