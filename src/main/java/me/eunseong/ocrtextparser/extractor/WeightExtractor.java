package me.eunseong.ocrtextparser.extractor;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.extractor.weight.NetWeightExtractor;
import me.eunseong.ocrtextparser.extractor.weight.TotalWeightExtractor;
import me.eunseong.ocrtextparser.extractor.weight.VehicleWeightExtractor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 무게 추출기 (조합기)
 * 총중량, 차중량, 실중량을 각각 추출하여 Map으로 반환
 */
@Component
public class WeightExtractor implements FieldExtractor<Map<String, Weight>> {

  private final TotalWeightExtractor totalWeightExtractor;
  private final VehicleWeightExtractor vehicleWeightExtractor;
  private final NetWeightExtractor netWeightExtractor;

  public WeightExtractor(
      TotalWeightExtractor totalWeightExtractor,
      VehicleWeightExtractor vehicleWeightExtractor,
      NetWeightExtractor netWeightExtractor) {
    this.totalWeightExtractor = totalWeightExtractor;
    this.vehicleWeightExtractor = vehicleWeightExtractor;
    this.netWeightExtractor = netWeightExtractor;
  }

  @Override
  public Optional<Map<String, Weight>> extract(OcrDocument document) {
    if (document == null) {
      return Optional.empty();
    }

    Map<String, Weight> weights = new HashMap<>();

    // 순서 중요: 더 구체적인 키워드부터 추출 (실중량, 차중량 먼저)
    // 그래야 "중량"이라는 일반적인 키워드가 잘못 매칭되지 않음

    netWeightExtractor.extract(document)
        .ifPresent(w -> weights.put("net", w));

    vehicleWeightExtractor.extract(document)
        .ifPresent(w -> weights.put("vehicle", w));

    totalWeightExtractor.extract(document)
        .ifPresent(w -> weights.put("total", w));

    return weights.isEmpty() ? Optional.empty() : Optional.of(weights);
  }
}
