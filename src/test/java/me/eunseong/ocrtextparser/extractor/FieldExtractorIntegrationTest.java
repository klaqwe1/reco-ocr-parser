package me.eunseong.ocrtextparser.extractor;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.util.OcrDocumentLoader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class FieldExtractorIntegrationTest {

  private final OcrDocumentLoader loader = new OcrDocumentLoader();
  private final DateExtractor dateExtractor = new DateExtractor();
  private final VehicleNumberExtractor vehicleExtractor = new VehicleNumberExtractor();
  private final WeightExtractor weightExtractor = new WeightExtractor();
  private final CompanyExtractor companyExtractor = new CompanyExtractor();

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
  @Disabled("Issue #4 이후 활성화 예정: sample_04에서 '차량 No.' 형식 미지원")
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
