package me.eunseong.ocrtextparser.extractor.weight;

import me.eunseong.ocrtextparser.extractor.strategy.ExtractionStrategy;
import me.eunseong.ocrtextparser.util.TextNormalizer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 차중량 추출기
 */
@Component
public class VehicleWeightExtractor extends AbstractWeightFieldExtractor {

  public VehicleWeightExtractor(List<ExtractionStrategy> strategies,
      TextNormalizer textNormalizer) {
    super(strategies, textNormalizer);
  }

  @Override
  protected String[] getKeywords() {
    // "중량"은 총중량이 아니고 차중량을 의미하는 경우가 많음 (sample_01 참고)
    return new String[]{"차중량", "공차중량", "중량"};
  }
}
