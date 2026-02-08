package me.eunseong.ocrtextparser.util;

import me.eunseong.ocrtextparser.config.ParserProperties;
import me.eunseong.ocrtextparser.domain.OcrWord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 좌표 기반 추출 헬퍼 유틸리티
 * OcrWord의 x, y 좌표를 이용한 위치 판단 로직 제공
 */
@Component
public class PositionHelper {

  private final ParserProperties parserProperties;

  public PositionHelper(ParserProperties parserProperties) {
    this.parserProperties = parserProperties;
  }

  /**
   * 두 단어가 같은 줄에 있는지 판단 (y좌표 기준)
   *
   * @param word1 단어 1
   * @param word2 단어 2
   * @return 같은 줄이면 true
   */
  public boolean isSameLine(OcrWord word1, OcrWord word2) {
    if (word1 == null || word2 == null) {
      return false;
    }

    int yTolerance = parserProperties.getPosition().getYTolerance();
    return Math.abs(word1.getY() - word2.getY()) <= yTolerance;
  }

  /**
   * 단어2가 단어1의 오른쪽에 있는지 판단
   *
   * @param leftWord 왼쪽 단어 (라벨)
   * @param rightWord 오른쪽 단어 (값 후보)
   * @return 오른쪽에 있고, 최소 거리 조건을 만족하면 true
   */
  public boolean isRightOf(OcrWord leftWord, OcrWord rightWord) {
    if (leftWord == null || rightWord == null) {
      return false;
    }

    int xMinOffset = parserProperties.getPosition().getXMinOffset();
    int leftWordEndX = leftWord.getX() + leftWord.getWidth();

    return rightWord.getX() >= leftWordEndX + xMinOffset;
  }

  /**
   * 라벨의 오른쪽에서 같은 줄에 있는 값을 찾기
   *
   * @param labelWord 라벨 단어
   * @param allWords 전체 단어 목록
   * @return 찾은 값 단어 (없으면 Optional.empty())
   */
  public Optional<OcrWord> findValueOnRight(OcrWord labelWord,
      List<OcrWord> allWords) {
    if (labelWord == null || allWords == null || allWords.isEmpty()) {
      return Optional.empty();
    }

    return allWords.stream()
        .filter(word -> isSameLine(labelWord, word))
        .filter(word -> isRightOf(labelWord, word))
        .findFirst();
  }

  /**
   * 라벨의 오른쪽에서 같은 줄에 있는 모든 값 찾기
   *
   * @param labelWord 라벨 단어
   * @param allWords 전체 단어 목록
   * @return 찾은 값 단어들 (없으면 빈 리스트)
   */
  public List<OcrWord> findAllValuesOnRight(OcrWord labelWord,
      List<OcrWord> allWords) {
    if (labelWord == null || allWords == null || allWords.isEmpty()) {
      return List.of();
    }

    return allWords.stream()
        .filter(word -> isSameLine(labelWord, word))
        .filter(word -> isRightOf(labelWord, word))
        .collect(Collectors.toList());
  }

  /**
   * 라벨 단어 바로 다음에 오는 값 단어 찾기 (가장 가까운 것)
   *
   * @param labelWord 라벨 단어
   * @param allWords 전체 단어 목록
   * @return 가장 가까운 값 단어 (없으면 Optional.empty())
   */
  public Optional<OcrWord> findClosestValueOnRight(OcrWord labelWord,
      List<OcrWord> allWords) {
    if (labelWord == null || allWords == null || allWords.isEmpty()) {
      return Optional.empty();
    }

    return allWords.stream()
        .filter(word -> isSameLine(labelWord, word))
        .filter(word -> isRightOf(labelWord, word))
        .min((w1, w2) -> Integer.compare(w1.getX(), w2.getX()));
  }
}
