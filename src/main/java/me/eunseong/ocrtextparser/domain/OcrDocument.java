package me.eunseong.ocrtextparser.domain;

import java.util.List;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrDocument {

  private String text; // 전체 텍스트
  private List<String> lines;  // 라인별 텍스트
  private List<OcrWord> words;  //  단어 목록 (좌표 포함)
  private Double confidence;  // OCR 신뢰도
}
