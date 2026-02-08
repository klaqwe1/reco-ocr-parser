package me.eunseong.ocrtextparser.validator;

import me.eunseong.ocrtextparser.domain.WeighingSlip;

import java.util.List;

/**
 * 계근지 검증 인터페이스
 * 추출된 데이터의 유효성 검증
 */
public interface Validator {

  /**
   * 검증 수행
   *
   * @param slip 검증할 계근지 데이터
   * @return 에러 메시지 목록 (빈 목록이면 검증 통과)
   */
  List<String> validate(WeighingSlip slip);

  /**
   * 검증 순서 반환 (낮을수록 먼저 실행)
   *
   * @return 검증 순서
   */
  int getOrder();
}
