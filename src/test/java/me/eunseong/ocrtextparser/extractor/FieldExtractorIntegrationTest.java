package me.eunseong.ocrtextparser.extractor;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.extractor.strategy.ExtractionStrategy;
import me.eunseong.ocrtextparser.extractor.strategy.PositionBasedStrategy;
import me.eunseong.ocrtextparser.extractor.strategy.TextBasedStrategy;
import me.eunseong.ocrtextparser.extractor.weight.NetWeightExtractor;
import me.eunseong.ocrtextparser.extractor.weight.TotalWeightExtractor;
import me.eunseong.ocrtextparser.extractor.weight.VehicleWeightExtractor;
import me.eunseong.ocrtextparser.config.ParserProperties;
import me.eunseong.ocrtextparser.util.OcrDocumentLoader;
import me.eunseong.ocrtextparser.util.PositionHelper;
import me.eunseong.ocrtextparser.util.TextMatcher;
import me.eunseong.ocrtextparser.util.TextNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class FieldExtractorIntegrationTest {

  private OcrDocumentLoader loader;
  private DateExtractor dateExtractor;
  private VehicleNumberExtractor vehicleExtractor;
  private WeightExtractor weightExtractor;
  private CompanyExtractor companyExtractor;

  @BeforeEach
  void setUp() {
    loader = new OcrDocumentLoader();

    // 의존성 생성
    TextNormalizer textNormalizer = new TextNormalizer();
    ParserProperties properties = new ParserProperties();
    properties.setFuzzyMatchThreshold(0.75);

    TextMatcher textMatcher = new TextMatcher(textNormalizer, properties);
    PositionHelper positionHelper = new PositionHelper(properties);

    // 전략 생성
    List<ExtractionStrategy> strategies = List.of(
        new TextBasedStrategy(textMatcher, textNormalizer),
        new PositionBasedStrategy(textMatcher, positionHelper)
    );

    // Extractor 생성
    dateExtractor = new DateExtractor(strategies, textNormalizer);
    vehicleExtractor = new VehicleNumberExtractor(strategies, textNormalizer);
    companyExtractor = new CompanyExtractor(strategies, textNormalizer);

    // Weight Extractor 생성
    TotalWeightExtractor totalWeightExtractor =
        new TotalWeightExtractor(strategies, textNormalizer);
    VehicleWeightExtractor vehicleWeightExtractor =
        new VehicleWeightExtractor(strategies, textNormalizer);
    NetWeightExtractor netWeightExtractor =
        new NetWeightExtractor(strategies, textNormalizer);

    weightExtractor = new WeightExtractor(
        totalWeightExtractor,
        vehicleWeightExtractor,
        netWeightExtractor
    );
  }

  @Test
  void extractAllFieldsFromSample01() throws Exception {
    // given
    OcrDocument document = loader.loadFromResource("samples/sample_01.json");

    // when
    Optional<LocalDate> date = dateExtractor.extract(document);
    Optional<String> vehicle = vehicleExtractor.extract(document);
    Optional<Map<String, Weight>> weights = weightExtractor.extract(document);
    Optional<String> company = companyExtractor.extract(document);

    // then
    assertThat(date).isPresent();
    assertThat(vehicle).isPresent();
    assertThat(weights).isPresent();
    assertThat(company).isPresent();

    // 결과 출력
    printExtractionResult("sample_01", date, vehicle, weights, company);
  }

  @Test
  void extractAllFieldsFromAllSamples() throws Exception {
    // given
    String[] samples = {
        "samples/sample_01.json",
        "samples/sample_02.json",
        "samples/sample_03.json",
        "samples/sample_04.json"
    };

    // when & then
    for (String sample : samples) {
      OcrDocument document = loader.loadFromResource(sample);

      Optional<LocalDate> date = dateExtractor.extract(document);
      Optional<String> vehicle = vehicleExtractor.extract(document);
      Optional<Map<String, Weight>> weights = weightExtractor.extract(document);
      Optional<String> company = companyExtractor.extract(document);

      // 검증
      assertThat(date).as(sample + " - date").isPresent();
      assertThat(vehicle).as(sample + " - vehicle").isPresent();
      assertThat(weights).as(sample + " - weights").isPresent();

      Map<String, Weight> w = weights.get();
      assertThat(w.get("total")).as(sample + " - total weight").isNotNull();
      assertThat(w.get("vehicle")).as(sample + " - vehicle weight").isNotNull();
      assertThat(w.get("net")).as(sample + " - net weight").isNotNull();

      // 결과 출력
      printExtractionResult(sample, date, vehicle, weights, company);
      System.out.println("\n" + "=".repeat(80) + "\n");
    }
  }

  /**
   * 추출 결과 출력
   */
  private void printExtractionResult(
      String sampleName,
      Optional<LocalDate> date,
      Optional<String> vehicle,
      Optional<Map<String, Weight>> weights,
      Optional<String> company
  ) {
    System.out.println("\n[" + sampleName + "] 추출 결과:");
    System.out.println("  날짜: " + date.orElse(null));
    System.out.println("  차량번호: " + vehicle.orElse(null));
    System.out.println("  거래처: " + company.orElse(null));

    if (weights.isPresent()) {
      Map<String, Weight> w = weights.get();
      System.out.println("  총중량: " + formatWeight(w.get("total")));
      System.out.println("  차중량: " + formatWeight(w.get("vehicle")));
      System.out.println("  실중량: " + formatWeight(w.get("net")));
    }
  }

  private String formatWeight(Weight weight) {
    if (weight == null) {
      return "null";
    }
    String result = weight.getValue() + " " + weight.getUnit();
    if (weight.getMeasuredAt() != null) {
      result += " (측정시간: " + weight.getMeasuredAt() + ")";
    }
    return result;
  }
}
