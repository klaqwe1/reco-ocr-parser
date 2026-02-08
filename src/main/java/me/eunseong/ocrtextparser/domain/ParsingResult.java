package me.eunseong.ocrtextparser.domain;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 파싱 결과
 * WeighingSlip 데이터 + 에러/경고 정보
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsingResult {

  private boolean success;
  private WeighingSlip data;
  private List<String> errors;
  private List<String> warnings;
  private Double confidence;

  /**
   * 성공 결과 생성
   *
   * @param data     파싱된 데이터
   * @param warnings 경고 목록
   * @return 성공 결과
   */
  public static ParsingResult success(WeighingSlip data, List<String> warnings) {
    return ParsingResult.builder()
        .success(true)
        .data(data)
        .errors(Collections.emptyList())
        .warnings(warnings != null ? warnings : Collections.emptyList())
        .confidence(1.0)
        .build();
  }

  /**
   * 성공 결과 생성 (경고 없음)
   *
   * @param data 파싱된 데이터
   * @return 성공 결과
   */
  public static ParsingResult success(WeighingSlip data) {
    return success(data, Collections.emptyList());
  }

  /**
   * 실패 결과 생성
   *
   * @param errors 에러 목록
   * @return 실패 결과
   */
  public static ParsingResult failure(List<String> errors) {
    return ParsingResult.builder()
        .success(false)
        .data(null)
        .errors(errors != null ? errors : Collections.emptyList())
        .warnings(Collections.emptyList())
        .confidence(0.0)
        .build();
  }

  /**
   * 실패 결과 생성 (단일 에러)
   *
   * @param error 에러 메시지
   * @return 실패 결과
   */
  public static ParsingResult failure(String error) {
    return failure(Collections.singletonList(error));
  }
}
