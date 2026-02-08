package me.eunseong.ocrtextparser.extractor;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.extractor.strategy.ExtractionStrategy;
import me.eunseong.ocrtextparser.util.TextNormalizer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 차량번호 추출기
 * 키워드: "차량번호", "차량No", "차번호" 등
 */
@Component
public class VehicleNumberExtractor extends AbstractFieldExtractor<String> {

  // 숫자+한글+숫자 또는 숫자만 (예: 12가3456, 8713)
  private static final Pattern VEHICLE_PATTERN =
      Pattern.compile("(\\d+[가-힣]*\\d+|\\d{4})");

  public VehicleNumberExtractor(List<ExtractionStrategy> strategies,
      TextNormalizer textNormalizer) {
    super(strategies, textNormalizer);
  }

  @Override
  protected String[] getKeywords() {
    return new String[]{"차량번호", "차량No", "차번호", "차량"};
  }

  @Override
  protected Optional<String> postProcess(String rawValue,
      OcrDocument document) {
    if (rawValue == null || rawValue.isEmpty()) {
      return Optional.empty();
    }

    // 공백 제거
    String cleaned = textNormalizer.removeWhitespace(rawValue);

    // 패턴 매칭
    Matcher matcher = VEHICLE_PATTERN.matcher(cleaned);
    if (matcher.find()) {
      String vehicleNumber = matcher.group(1);
      return Optional.of(vehicleNumber);
    }

    return Optional.empty();
  }
}
