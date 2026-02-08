package me.eunseong.ocrtextparser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.ParsingResult;
import me.eunseong.ocrtextparser.pipeline.ParsingPipeline;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 파싱 서비스
 * OCR 문서를 계근지 데이터로 파싱
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParsingService {

  private final ParsingPipeline pipeline;

  /**
   * OCR 문서 파싱
   *
   * @param document OCR 문서
   * @return 파싱 결과
   */
  public ParsingResult parse(OcrDocument document) {
    if (document == null) {
      log.error("파싱 실패: OCR 문서가 null입니다.");
      return ParsingResult.failure("OCR 문서가 null입니다.");
    }

    log.info("파싱 시작: {} lines, {} words",
        document.getLines() != null ? document.getLines().size() : 0,
        document.getWords() != null ? document.getWords().size() : 0);

    try {
      ParsingResult result = pipeline.process(document);

      if (result.isSuccess()) {
        log.info("파싱 성공");
        if (result.getWarnings() != null && !result.getWarnings().isEmpty()) {
          log.warn("경고 발생: {}", result.getWarnings());
        }
      } else {
        log.warn("파싱 실패: {} errors",
            result.getErrors() != null ? result.getErrors().size() : 0);
        if (result.getErrors() != null) {
          result.getErrors().forEach(error -> log.warn("  - {}", error));
        }
      }

      return result;
    } catch (Exception e) {
      log.error("파싱 중 예외 발생", e);
      return ParsingResult.failure(List.of("파싱 중 예외 발생: " + e.getMessage()));
    }
  }
}
