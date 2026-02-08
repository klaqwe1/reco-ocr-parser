package me.eunseong.ocrtextparser.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.ParsingResult;
import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.domain.WeighingSlip;
import me.eunseong.ocrtextparser.service.ParsingService;
import me.eunseong.ocrtextparser.util.OcrDocumentLoader;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 샘플 파일 파싱 데모
 * 애플리케이션 시작 시 자동으로 모든 샘플 파일을 파싱하고 결과를 출력
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SampleParsingDemo implements CommandLineRunner {

  private final ParsingService parsingService;
  private final OcrDocumentLoader documentLoader;

  @Override
  public void run(String... args) throws Exception {
    log.info("=".repeat(80));
    log.info("샘플 파일 파싱 데모 시작");
    log.info("=".repeat(80));

    String[] samples = {
        "samples/sample_01.json",
        "samples/sample_02.json",
        "samples/sample_03.json",
        "samples/sample_04.json"
    };

    int successCount = 0;
    int failCount = 0;

    for (String samplePath : samples) {
      try {
        log.info("\n");
        log.info("━".repeat(80));
        log.info("파일: {}", samplePath);
        log.info("━".repeat(80));

        // 1. OCR 문서 로드
        OcrDocument document = documentLoader.loadFromResource(samplePath);
        log.info("문서 로드 완료: {} lines, {} words",
            document.getLines().size(),
            document.getWords().size());

        // 2. 파싱 실행
        ParsingResult result = parsingService.parse(document);

        // 3. 결과 출력
        if (result.isSuccess()) {
          successCount++;
          printSuccessResult(result);
        } else {
          failCount++;
          printFailureResult(result);
        }

      } catch (Exception e) {
        log.error("파일 처리 중 예외 발생: {}", samplePath, e);
        failCount++;
      }
    }

    // 4. 최종 요약
    log.info("\n");
    log.info("=".repeat(80));
    log.info("파싱 완료 - 성공: {}, 실패: {}, 전체: {}",
        successCount, failCount, samples.length);
    log.info("=".repeat(80));
  }

  /**
   * 성공 결과 출력
   */
  private void printSuccessResult(ParsingResult result) {
    WeighingSlip slip = result.getData();

    log.info("✅ 파싱 성공");
    log.info("");
    log.info("📋 계근지 정보:");
    log.info("  - 날짜: {}", slip.getDate());
    log.info("  - 차량번호: {}", slip.getVehicleNumber());
    log.info("  - 거래처: {}", slip.getCompany() != null ? slip.getCompany() : "(없음)");
    log.info("  - 품명: {}", slip.getProductName() != null ? slip.getProductName() : "(없음)");
    log.info("");
    log.info("⚖️  무게 정보:");
    log.info("  - 총중량: {}", formatWeight(slip.getTotalWeight()));
    log.info("  - 차중량: {}", formatWeight(slip.getVehicleWeight()));
    log.info("  - 실중량: {}", formatWeight(slip.getNetWeight()));

    // 무게 계산 검증
    if (slip.getTotalWeight() != null &&
        slip.getVehicleWeight() != null &&
        slip.getNetWeight() != null) {
      double calculated = slip.getTotalWeight().getValue() - slip.getVehicleWeight().getValue();
      double actual = slip.getNetWeight().getValue();
      double diff = Math.abs(calculated - actual);

      log.info("");
      log.info("🔍 무게 계산 검증:");
      log.info("  - 계산값: {} kg (총중량 - 차중량)", String.format("%.2f", calculated));
      log.info("  - 실제값: {} kg", String.format("%.2f", actual));
      log.info("  - 차이: {} kg", String.format("%.2f", diff));
      log.info("  - 검증: {}", diff <= 10.0 ? "✅ 통과" : "❌ 실패");
    }

    // 경고 출력
    if (result.getWarnings() != null && !result.getWarnings().isEmpty()) {
      log.info("");
      log.info("⚠️  경고:");
      result.getWarnings().forEach(warning -> log.info("  - {}", warning));
    }
  }

  /**
   * 실패 결과 출력
   */
  private void printFailureResult(ParsingResult result) {
    log.error("❌ 파싱 실패");
    log.error("");
    log.error("🚫 에러:");
    if (result.getErrors() != null) {
      result.getErrors().forEach(error -> log.error("  - {}", error));
    }

    if (result.getWarnings() != null && !result.getWarnings().isEmpty()) {
      log.warn("");
      log.warn("⚠️  경고:");
      result.getWarnings().forEach(warning -> log.warn("  - {}", warning));
    }
  }

  /**
   * 무게 정보 포맷팅
   */
  private String formatWeight(Weight weight) {
    if (weight == null) {
      return "(없음)";
    }

    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%.2f %s", weight.getValue(), weight.getUnit()));

    if (weight.getMeasuredAt() != null) {
      sb.append(String.format(" (측정: %s)", weight.getMeasuredAt()));
    }

    return sb.toString();
  }
}
