package me.eunseong.ocrtextparser.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.parser")
@Getter
@Setter
public class ParserProperties {

  /**
   * Fuzzy Matching 임계값 (0.0 ~ 1.0) (임의값)
   */
  private double fuzzyMatchThreshold = 0.8;

  /**
   * 좌표 기반 추출 설정
   */
  private Position position = new Position();

  /**
   * 검증 설정
   */
  private Validation validation = new Validation();

  @Getter
  @Setter
  public static class Position {

    /**
     * y좌표 허용 오차 (픽셀) (임의값)
     */
    private int yTolerance = 80;

    /**
     * 라벨 오른쪽 최소 거리 (픽셀) (임의값)
     */
    private int xMinOffset = 50;
  }

  @Getter
  @Setter
  public static class Validation {

    /**
     * 무게 오차 허용 (kg) (임의값)
     */
    private double weightTolerance = 10.0;
  }
}
