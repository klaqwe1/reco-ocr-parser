package me.eunseong.ocrtextparser.domain;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeighingSlip {

  private LocalDate date;  // 계랑 일자
  private String vehicleNumber;  // 차량 번호
  private String company;  // 상호
  private String productName;  // 품명
  private Weight totalWeight;  // 총 중량
  private Weight vehicleWeight;  // 차량 중량
  private Weight netWeight;  //  실중량
  private String issuer;  // 발행처
  private String coordinates;  // 좌표

}
