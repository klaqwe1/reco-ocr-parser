package me.eunseong.ocrtextparser.extractor.strategy;

import me.eunseong.ocrtextparser.domain.OcrDocument;
import me.eunseong.ocrtextparser.domain.OcrWord;
import me.eunseong.ocrtextparser.util.PositionHelper;
import me.eunseong.ocrtextparser.util.TextMatcher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 좌표 기반 추출 전략
 * OcrWord의 x, y 좌표를 이용하여 라벨-값 매칭
 * 라벨 찾기 → 라벨 오른쪽 + 같은 줄에서 값 찾기
 */
@Component
public class PositionBasedStrategy implements ExtractionStrategy {

  private final TextMatcher textMatcher;
  private final PositionHelper positionHelper;

  public PositionBasedStrategy(TextMatcher textMatcher,
      PositionHelper positionHelper) {
    this.textMatcher = textMatcher;
    this.positionHelper = positionHelper;
  }

  @Override
  public Optional<String> extract(OcrDocument document, String[] keywords) {
    if (document == null || document.getWords() == null ||
        keywords == null || keywords.length == 0) {
      return Optional.empty();
    }

    List<OcrWord> words = document.getWords();

    // 1. 라벨 찾기 (키워드 매칭)
    Optional<OcrWord> labelWord = findLabelWord(words, keywords);
    if (labelWord.isEmpty()) {
      return Optional.empty();
    }

    // 2. 라벨의 오른쪽 + 같은 줄에서 값 찾기
    Optional<OcrWord> valueWord =
        positionHelper.findClosestValueOnRight(labelWord.get(), words);

    return valueWord.map(OcrWord::getText);
  }

  @Override
  public boolean supports(OcrDocument document) {
    // words 정보가 있으면 사용 가능
    return document != null && document.getWords() != null &&
        !document.getWords().isEmpty();
  }

  @Override
  public int getPriority() {
    // 우선순위 2 (좌표 기반은 텍스트 기반 실패 시 시도)
    return 2;
  }

  /**
   * 키워드에 해당하는 라벨 단어 찾기
   *
   * @param words 전체 단어 목록
   * @param keywords 키워드 배열
   * @return 라벨 단어 (없으면 Optional.empty())
   */
  private Optional<OcrWord> findLabelWord(List<OcrWord> words,
      String[] keywords) {
    for (OcrWord word : words) {
      if (textMatcher.matches(word.getText(), keywords)) {
        return Optional.of(word);
      }
    }
    return Optional.empty();
  }
}
