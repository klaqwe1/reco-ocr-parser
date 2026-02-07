package me.eunseong.ocrtextparser.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ParserPropertiesTest {

  @Autowired
  private ParserProperties properties;

  @Test
  void propertiesLoaded() {
    // then
    assertThat(properties).isNotNull();
    assertThat(properties.getFuzzyMatchThreshold()).isEqualTo(0.8);
    assertThat(properties.getPosition().getYTolerance()).isEqualTo(80);
    assertThat(properties.getPosition().getXMinOffset()).isEqualTo(50);
    assertThat(properties.getValidation().getWeightTolerance()).isEqualTo(10.0);

    System.out.println("Fuzzy Match Threshold: " + properties.getFuzzyMatchThreshold());
    System.out.println("Y Tolerance: " + properties.getPosition().getYTolerance());
    System.out.println("X Min Offset: " + properties.getPosition().getXMinOffset());
    System.out.println("Weight Tolerance: " + properties.getValidation().getWeightTolerance());
  }
}