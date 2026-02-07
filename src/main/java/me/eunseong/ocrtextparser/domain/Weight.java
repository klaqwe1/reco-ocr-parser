package me.eunseong.ocrtextparser.domain;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Weight {

  private Double value;  // 무게
  private String unit;  // 단위
  private LocalDate measuredAt;  // 측정 시간

}
