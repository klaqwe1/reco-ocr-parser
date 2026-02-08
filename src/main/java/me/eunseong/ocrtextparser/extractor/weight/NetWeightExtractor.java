package me.eunseong.ocrtextparser.extractor.weight;

import me.eunseong.ocrtextparser.extractor.strategy.ExtractionStrategy;
import me.eunseong.ocrtextparser.util.TextNormalizer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 실중량 추출기
 */
@Component
public class NetWeightExtractor extends AbstractWeightFieldExtractor {

  public NetWeightExtractor(List<ExtractionStrategy> strategies,
      TextNormalizer textNormalizer) {
    super(strategies, textNormalizer);
  }

  @Override
  protected String[] getKeywords() {
    return new String[]{"실중량", "실 중량"};
  }
}
