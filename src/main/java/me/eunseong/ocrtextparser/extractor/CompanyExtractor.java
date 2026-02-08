package me.eunseong.ocrtextparser.extractor;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.extractor.strategy.ExtractionStrategy;
import me.eunseong.ocrtextparser.util.TextNormalizer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 거래처/상호 추출기
 * 키워드: "거래처", "상호" 등
 */
@Component
public class CompanyExtractor extends AbstractFieldExtractor<String> {

  public CompanyExtractor(List<ExtractionStrategy> strategies,
      TextNormalizer textNormalizer) {
    super(strategies, textNormalizer);
  }

  @Override
  protected String[] getKeywords() {
    return new String[]{"거래처", "상호"};
  }

  @Override
  protected Optional<String> postProcess(String rawValue,
      OcrDocument document) {
    if (rawValue == null || rawValue.isEmpty()) {
      return Optional.empty();
    }

    // 콜론, 공백 제거 후 정리
    String cleaned = textNormalizer.removeSpecialChars(rawValue).trim();

    if (cleaned.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(cleaned);
  }
}
