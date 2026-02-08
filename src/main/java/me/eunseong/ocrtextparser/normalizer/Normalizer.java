package me.eunseong.ocrtextparser.normalizer;

/**
 * 값 정규화 인터페이스
 * 추출된 원본 값을 표준화된 형식으로 변환
 *
 * @param <T> 정규화할 값의 타입
 */
public interface Normalizer<T> {

  /**
   * 값 정규화
   *
   * @param value 원본 값
   * @return 정규화된 값 (정규화 실패 시 원본 반환)
   */
  T normalize(T value);

  /**
   * 지원하는 타입 반환
   *
   * @return 지원하는 클래스 타입
   */
  Class<T> getSupportedType();
}
