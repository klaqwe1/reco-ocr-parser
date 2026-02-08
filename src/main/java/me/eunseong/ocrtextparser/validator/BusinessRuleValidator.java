package me.eunseong.ocrtextparser.validator;

import me.eunseong.ocrtextparser.config.ParserProperties;
import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.domain.WeighingSlip;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 비즈니스 룰 검증기
 * 실중량 = 총중량 - 차중량 검증
 */
@Component
public class BusinessRuleValidator implements Validator {

  private final ParserProperties parserProperties;

  public BusinessRuleValidator(ParserProperties parserProperties) {
    this.parserProperties = parserProperties;
  }

  @Override
  public List<String> validate(WeighingSlip slip) {
    List<String> errors = new ArrayList<>();

    if (slip == null) {
      return errors;
    }

    // 무게 필드가 모두 존재하는지 확인
    Weight totalWeight = slip.getTotalWeight();
    Weight vehicleWeight = slip.getVehicleWeight();
    Weight netWeight = slip.getNetWeight();

    if (totalWeight == null || vehicleWeight == null || netWeight == null) {
      // 필수 필드 검증은 RequiredFieldValidator가 담당
      return errors;
    }

    if (totalWeight.getValue() == null || vehicleWeight.getValue() == null ||
        netWeight.getValue() == null) {
      return errors;
    }

    // 비즈니스 룰: 실중량 = 총중량 - 차중량
    double expectedNetWeight =
        totalWeight.getValue() - vehicleWeight.getValue();
    double actualNetWeight = netWeight.getValue();
    double tolerance = parserProperties.getValidation().getWeightTolerance();

    double difference = Math.abs(expectedNetWeight - actualNetWeight);

    if (difference > tolerance) {
      errors.add(String.format(
          "무게 계산이 맞지 않습니다. 실중량(%.2f) != 총중량(%.2f) - 차중량(%.2f) (차이: %.2f kg, 허용오차: %.2f kg)",
          actualNetWeight,
          totalWeight.getValue(),
          vehicleWeight.getValue(),
          difference,
          tolerance
      ));
    }

    // 추가 검증: 총중량 >= 차중량
    if (totalWeight.getValue() < vehicleWeight.getValue()) {
      errors.add(String.format(
          "총중량(%.2f kg)이 차중량(%.2f kg)보다 작습니다",
          totalWeight.getValue(),
          vehicleWeight.getValue()
      ));
    }

    // 추가 검증: 실중량 >= 0
    if (actualNetWeight < 0) {
      errors.add(String.format(
          "실중량(%.2f kg)이 음수입니다",
          actualNetWeight
      ));
    }

    return errors;
  }

  @Override
  public int getOrder() {
    // 필수 필드 검증 이후에 실행
    return 2;
  }
}
