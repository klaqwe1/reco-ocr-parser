package me.eunseong.ocrtextparser.service;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.ParsingResult;
import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.domain.WeighingSlip;
import me.eunseong.ocrtextparser.util.OcrDocumentLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 파싱 서비스 통합 테스트
 * end-to-end 파싱 검증
 */
@SpringBootTest
class ParsingServiceIntegrationTest {

  @Autowired
  private ParsingService parsingService;

  @Autowired
  private OcrDocumentLoader loader;

  @Test
  @DisplayName("sample_01 파싱 성공")
  void parseSample01() throws Exception {
    // given
    OcrDocument document = loader.loadFromResource("samples/sample_01.json");

    // when
    ParsingResult result = parsingService.parse(document);

    // then
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getData()).isNotNull();

    WeighingSlip slip = result.getData();
    assertThat(slip.getDate()).isEqualTo(LocalDate.of(2026, 2, 2));
    assertThat(slip.getVehicleNumber()).isEqualTo("8713");

    // 무게 검증
    assertThat(slip.getTotalWeight()).isNotNull();
    assertThat(slip.getTotalWeight().getValue()).isEqualTo(12480.0);
    assertThat(slip.getTotalWeight().getUnit()).isEqualTo("kg");

    assertThat(slip.getVehicleWeight()).isNotNull();
    assertThat(slip.getVehicleWeight().getValue()).isEqualTo(7470.0);
    assertThat(slip.getVehicleWeight().getUnit()).isEqualTo("kg");

    assertThat(slip.getNetWeight()).isNotNull();
    assertThat(slip.getNetWeight().getValue()).isEqualTo(5010.0);
    assertThat(slip.getNetWeight().getUnit()).isEqualTo("kg");

    // 무게 계산 검증 (총중량 - 차중량 = 실중량)
    double calculatedNet = slip.getTotalWeight().getValue() - slip.getVehicleWeight().getValue();
    assertThat(slip.getNetWeight().getValue()).isCloseTo(calculatedNet, org.assertj.core.data.Offset.offset(10.0));

    // 결과 출력
    printResult("sample_01", result);
  }

  @Test
  @DisplayName("sample_02 파싱 성공")
  void parseSample02() throws Exception {
    // given
    OcrDocument document = loader.loadFromResource("samples/sample_02.json");

    // when
    ParsingResult result = parsingService.parse(document);

    // then
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getData()).isNotNull();

    WeighingSlip slip = result.getData();
    assertThat(slip.getDate()).isNotNull();
    assertThat(slip.getVehicleNumber()).isNotNull();
    assertThat(slip.getTotalWeight()).isNotNull();
    assertThat(slip.getVehicleWeight()).isNotNull();
    assertThat(slip.getNetWeight()).isNotNull();

    // 무게 계산 검증
    double calculatedNet = slip.getTotalWeight().getValue() - slip.getVehicleWeight().getValue();
    assertThat(slip.getNetWeight().getValue()).isCloseTo(calculatedNet, org.assertj.core.data.Offset.offset(10.0));

    // 결과 출력
    printResult("sample_02", result);
  }

  @Test
  @DisplayName("sample_03 파싱 성공")
  void parseSample03() throws Exception {
    // given
    OcrDocument document = loader.loadFromResource("samples/sample_03.json");

    // when
    ParsingResult result = parsingService.parse(document);

    // then
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getData()).isNotNull();

    WeighingSlip slip = result.getData();
    assertThat(slip.getDate()).isNotNull();
    assertThat(slip.getVehicleNumber()).isNotNull();
    assertThat(slip.getTotalWeight()).isNotNull();
    assertThat(slip.getVehicleWeight()).isNotNull();
    assertThat(slip.getNetWeight()).isNotNull();

    // 무게 계산 검증
    double calculatedNet = slip.getTotalWeight().getValue() - slip.getVehicleWeight().getValue();
    assertThat(slip.getNetWeight().getValue()).isCloseTo(calculatedNet, org.assertj.core.data.Offset.offset(10.0));

    // 결과 출력
    printResult("sample_03", result);
  }

  @Test
  @DisplayName("sample_04 파싱 성공")
  void parseSample04() throws Exception {
    // given
    OcrDocument document = loader.loadFromResource("samples/sample_04.json");

    // when
    ParsingResult result = parsingService.parse(document);

    // then
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getData()).isNotNull();

    WeighingSlip slip = result.getData();
    assertThat(slip.getDate()).isNotNull();
    assertThat(slip.getVehicleNumber()).isNotNull();
    assertThat(slip.getTotalWeight()).isNotNull();
    assertThat(slip.getVehicleWeight()).isNotNull();
    assertThat(slip.getNetWeight()).isNotNull();

    // 무게 계산 검증
    double calculatedNet = slip.getTotalWeight().getValue() - slip.getVehicleWeight().getValue();
    assertThat(slip.getNetWeight().getValue()).isCloseTo(calculatedNet, org.assertj.core.data.Offset.offset(10.0));

    // 결과 출력
    printResult("sample_04", result);
  }

  @Test
  @DisplayName("모든 샘플 파일 파싱 성공")
  void parseAllSamples() throws Exception {
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
      ParsingResult result = parsingService.parse(document);

      assertThat(result.isSuccess())
          .as(sample + " should parse successfully")
          .isTrue();

      assertThat(result.getData())
          .as(sample + " should have data")
          .isNotNull();

      WeighingSlip slip = result.getData();

      // 필수 필드 검증
      assertThat(slip.getDate()).as(sample + " - date").isNotNull();
      assertThat(slip.getVehicleNumber()).as(sample + " - vehicleNumber").isNotNull();
      assertThat(slip.getTotalWeight()).as(sample + " - totalWeight").isNotNull();
      assertThat(slip.getVehicleWeight()).as(sample + " - vehicleWeight").isNotNull();
      assertThat(slip.getNetWeight()).as(sample + " - netWeight").isNotNull();

      // 무게 계산 검증
      double calculatedNet = slip.getTotalWeight().getValue() - slip.getVehicleWeight().getValue();
      assertThat(slip.getNetWeight().getValue())
          .as(sample + " - weight calculation")
          .isCloseTo(calculatedNet, org.assertj.core.data.Offset.offset(10.0));

      printResult(sample, result);
      System.out.println("\n" + "=".repeat(80) + "\n");
    }
  }

  @Test
  @DisplayName("null 문서 파싱 실패")
  void parseNullDocument() {
    // when
    ParsingResult result = parsingService.parse(null);

    // then
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getErrors()).isNotEmpty();
    assertThat(result.getErrors().get(0)).contains("null");
  }

  /**
   * 파싱 결과 출력
   */
  private void printResult(String sampleName, ParsingResult result) {
    System.out.println("\n[" + sampleName + "] 파싱 결과:");
    System.out.println("  성공: " + result.isSuccess());

    if (result.isSuccess()) {
      WeighingSlip slip = result.getData();
      System.out.println("  날짜: " + slip.getDate());
      System.out.println("  차량번호: " + slip.getVehicleNumber());
      System.out.println("  거래처: " + slip.getCompany());
      System.out.println("  총중량: " + formatWeight(slip.getTotalWeight()));
      System.out.println("  차중량: " + formatWeight(slip.getVehicleWeight()));
      System.out.println("  실중량: " + formatWeight(slip.getNetWeight()));

      if (result.getWarnings() != null && !result.getWarnings().isEmpty()) {
        System.out.println("  경고:");
        result.getWarnings().forEach(w -> System.out.println("    - " + w));
      }
    } else {
      System.out.println("  에러:");
      result.getErrors().forEach(e -> System.out.println("    - " + e));
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
