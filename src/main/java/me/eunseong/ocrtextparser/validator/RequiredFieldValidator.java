package me.eunseong.ocrtextparser.validator;

import me.eunseong.ocrtextparser.domain.WeighingSlip;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 필수 필드 검증기
 * 필수 필드가 누락되었는지 확인
 */
@Component
public class RequiredFieldValidator implements Validator {

  @Override
  public List<String> validate(WeighingSlip slip) {
    List<String> errors = new ArrayList<>();

    if (slip == null) {
      errors.add("계근지 데이터가 null입니다");
      return errors;
    }

    // 필수 필드 검증
    if (slip.getDate() == null) {
      errors.add("날짜가 누락되었습니다");
    }

    if (slip.getVehicleNumber() == null || slip.getVehicleNumber().isEmpty()) {
      errors.add("차량번호가 누락되었습니다");
    }

    // 무게 필드 검증
    if (slip.getTotalWeight() == null) {
      errors.add("총중량이 누락되었습니다");
    }

    if (slip.getVehicleWeight() == null) {
      errors.add("차중량이 누락되었습니다");
    }

    if (slip.getNetWeight() == null) {
      errors.add("실중량이 누락되었습니다");
    }

    // 거래처는 선택적 필드 (검증하지 않음)
    // 품목명도 선택적 필드

    return errors;
  }

  @Override
  public int getOrder() {
    // 필수 필드 검증이 가장 먼저 실행되어야 함
    return 1;
  }
}
