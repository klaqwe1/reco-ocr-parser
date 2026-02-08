package me.eunseong.ocrtextparser.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Weight {

  private Double value;  // 무게
  private String unit;  // 단위
  private LocalDateTime measuredAt;  // 측정 시간

}
