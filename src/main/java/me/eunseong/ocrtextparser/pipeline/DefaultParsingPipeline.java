package me.eunseong.ocrtextparser.pipeline;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.ParsingResult;
import me.eunseong.ocrtextparser.domain.Weight;
import me.eunseong.ocrtextparser.domain.WeighingSlip;
import me.eunseong.ocrtextparser.extractor.ExtractorRegistry;
import me.eunseong.ocrtextparser.normalizer.DateNormalizer;
import me.eunseong.ocrtextparser.normalizer.WeightNormalizer;
import me.eunseong.ocrtextparser.validator.Validator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 기본 파싱 파이프라인 구현
 * 추출 → 정규화 → 검증 단계를 순차 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultParsingPipeline implements ParsingPipeline {

  private final ExtractorRegistry extractorRegistry;
  private final DateNormalizer dateNormalizer;
  private final WeightNormalizer weightNormalizer;
  private final List<Validator> validators;

  @Override
  public ParsingResult process(OcrDocument document) {
    if (document == null) {
      return ParsingResult.failure("OCR 문서가 null입니다.");
    }

    // 1. Context 생성
    ParsingContext context = ParsingContext.builder()
        .document(document)
        .slipBuilder(WeighingSlip.builder())
        .errors(new ArrayList<>())
        .warnings(new ArrayList<>())
        .build();

    // 2. 추출
    extractFields(context);

    // 3. 정규화
    normalizeFields(context);

    // 4. 검증
    validateFields(context);

    // 5. 결과 생성
    return buildResult(context);
  }

  /**
   * 필드 추출
   * ExtractorRegistry로 모든 필드 추출
   */
  private void extractFields(ParsingContext context) {
    log.debug("필드 추출 시작");

    // 날짜 추출
    extractorRegistry.getExtractor("date").ifPresent(extractor -> {
      try {
        extractor.extract(context.getDocument()).ifPresent(value -> {
          if (value instanceof LocalDate) {
            context.getSlipBuilder().date((LocalDate) value);
          }
        });
      } catch (Exception e) {
        log.warn("날짜 추출 중 예외 발생: {}", e.getMessage());
        context.addWarning("날짜 추출 중 예외 발생: " + e.getMessage());
      }
    });

    // 차량번호 추출
    extractorRegistry.getExtractor("vehicleNumber").ifPresent(extractor -> {
      try {
        extractor.extract(context.getDocument()).ifPresent(value -> {
          if (value instanceof String) {
            context.getSlipBuilder().vehicleNumber((String) value);
          }
        });
      } catch (Exception e) {
        log.warn("차량번호 추출 중 예외 발생: {}", e.getMessage());
        context.addWarning("차량번호 추출 중 예외 발생: " + e.getMessage());
      }
    });

    // 회사명 추출
    extractorRegistry.getExtractor("company").ifPresent(extractor -> {
      try {
        extractor.extract(context.getDocument()).ifPresent(value -> {
          if (value instanceof String) {
            context.getSlipBuilder().company((String) value);
          }
        });
      } catch (Exception e) {
        log.warn("회사명 추출 중 예외 발생: {}", e.getMessage());
        context.addWarning("회사명 추출 중 예외 발생: " + e.getMessage());
      }
    });

    // 무게 추출
    extractorRegistry.getExtractor("weight").ifPresent(extractor -> {
      try {
        extractor.extract(context.getDocument()).ifPresent(value -> {
          if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Weight> weights = (Map<String, Weight>) value;

            if (weights.containsKey("total")) {
              context.getSlipBuilder().totalWeight(weights.get("total"));
            }
            if (weights.containsKey("vehicle")) {
              context.getSlipBuilder().vehicleWeight(weights.get("vehicle"));
            }
            if (weights.containsKey("net")) {
              context.getSlipBuilder().netWeight(weights.get("net"));
            }
          }
        });
      } catch (Exception e) {
        log.warn("무게 추출 중 예외 발생: {}", e.getMessage());
        context.addWarning("무게 추출 중 예외 발생: " + e.getMessage());
      }
    });

    log.debug("필드 추출 완료");
  }

  /**
   * 필드 정규화
   * Normalizer 적용
   */
  private void normalizeFields(ParsingContext context) {
    log.debug("필드 정규화 시작");

    WeighingSlip.WeighingSlipBuilder builder = context.getSlipBuilder();

    // 날짜 정규화 (현재는 pass-through지만 확장 가능)
    // builder에서 가져오기 위해 임시로 build()를 호출할 수 없으므로
    // 정규화는 이미 추출된 값에 대해 적용
    // 실제로 builder에 저장된 값을 정규화하려면 build() 후 재설정 필요

    // 무게 정규화
    WeighingSlip tempSlip = builder.build();

    if (tempSlip.getTotalWeight() != null) {
      Weight normalized = weightNormalizer.normalize(tempSlip.getTotalWeight());
      builder.totalWeight(normalized);
    }

    if (tempSlip.getVehicleWeight() != null) {
      Weight normalized = weightNormalizer.normalize(tempSlip.getVehicleWeight());
      builder.vehicleWeight(normalized);
    }

    if (tempSlip.getNetWeight() != null) {
      Weight normalized = weightNormalizer.normalize(tempSlip.getNetWeight());
      builder.netWeight(normalized);
    }

    log.debug("필드 정규화 완료");
  }

  /**
   * 필드 검증
   * Validator 실행 (order 순서대로)
   */
  private void validateFields(ParsingContext context) {
    log.debug("필드 검증 시작");

    // 임시로 WeighingSlip 생성하여 검증
    WeighingSlip slip = context.getSlipBuilder().build();

    // Validator들을 order 순서대로 정렬하여 실행
    validators.stream()
        .sorted(Comparator.comparingInt(Validator::getOrder))
        .forEach(validator -> {
          try {
            List<String> errors = validator.validate(slip);
            errors.forEach(context::addError);
          } catch (Exception e) {
            log.warn("검증 중 예외 발생: {}", e.getMessage());
            context.addError("검증 중 예외 발생: " + e.getMessage());
          }
        });

    log.debug("필드 검증 완료 - 에러: {}, 경고: {}",
        context.getErrors().size(),
        context.getWarnings().size());
  }

  /**
   * 결과 생성
   * WeighingSlip + errors/warnings 반환
   */
  private ParsingResult buildResult(ParsingContext context) {
    WeighingSlip slip = context.getSlipBuilder().build();

    if (context.hasErrors()) {
      // 에러가 있으면 실패
      return ParsingResult.failure(context.getErrors());
    } else {
      // 에러가 없으면 성공 (경고는 포함 가능)
      return ParsingResult.success(slip, context.getWarnings());
    }
  }
}
