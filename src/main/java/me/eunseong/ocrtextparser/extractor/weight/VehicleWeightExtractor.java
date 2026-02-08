package me.eunseong.ocrtextparser.extractor.weight;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.extractor.strategy.ExtractionStrategy;
import me.eunseong.ocrtextparser.util.TextNormalizer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
    // Only use specific keywords to avoid matching "총중량" or "실중량"
    // Note: sample_01 has "중 량:" on one line and value on next line,
    // which is handled by TextBasedStrategy's extractValueFromNextLine
    return new String[]{"차중량", "공차중량", "차량중량", "공차", "차중"};
  }

  @Override
  protected Optional<Weight> extractWithFallback(OcrDocument document) {
    // Fallback for sample_01: look for a line with "중량" but not "총중량" or "실중량"
    // and extract from current or next line
    if (document == null || document.getLines() == null) {
      return Optional.empty();
    }

    List<String> lines = document.getLines();
    for (int i = 0; i < lines.size(); i++) {
      String line = textNormalizer.normalize(lines.get(i));
      // Check for "중량" pattern (not "총중량" or "실중량")
      if (line.contains("중량") &&
          !line.contains("총중량") &&
          !line.contains("실중량")) {
        // This line has "중량" without "총" or "실" prefix
        // Try extracting from current line first
        String currentLine = lines.get(i);
        Optional<String> rawValue = extractWeightFromLine(currentLine);
        if (rawValue.isPresent()) {
          return postProcess(rawValue.get(), document);
        }

        // If no value on current line, try next line
        if (i + 1 < lines.size()) {
          String nextLine = lines.get(i + 1);
          Optional<String> nextValue = extractWeightFromLine(nextLine);
          if (nextValue.isPresent()) {
            return postProcess(nextValue.get(), document);
          }
        }
      }
    }

    return Optional.empty();
  }

  /**
   * Extract weight pattern from a line
   */
  private Optional<String> extractWeightFromLine(String line) {
    // Remove timestamp and extract weight
    String cleaned = line.replaceAll("\\d{2}\\s*:\\s*\\d{2}(\\s*:\\s*\\d{2})?\\s*", "").trim();
    if (cleaned.matches(".*[\\d,\\s]+\\s*kg.*")) {
      return Optional.of(cleaned);
    }
    return Optional.empty();
  }
}
