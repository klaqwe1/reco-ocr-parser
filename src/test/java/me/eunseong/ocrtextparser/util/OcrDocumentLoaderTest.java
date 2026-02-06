package me.eunseong.ocrtextparser.util;

import java.io.IOException;
import me.eunseong.ocrtextparser.domain.OcrDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class OcrDocumentLoaderTest {

  private final OcrDocumentLoader loader = new OcrDocumentLoader();


  @Test
  @DisplayName("sample이 제대로 load 되는지 확인")
  void loadAllSamples() throws IOException {
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
      assertThat(document).isNotNull();
      assertThat(document.getText()).contains("계");
      assertThat(document.getLines()).hasSizeGreaterThan(0);
      System.out.println(sample + " loaded successfully");
      // 결과 출력
      printDocument(document, "sample_" + sample.charAt(16));
    }

  }

  @Test
  @DisplayName("document의 내용이 포함되었는 지 확인")
  void sample01ShouldContainExpectedData() throws IOException {
    // given
    String resourcePath = "samples/sample_01.json";

    // when
    OcrDocument document = loader.loadFromResource(resourcePath);

    // then
    assertThat(document.getText()).contains("계량일자");
    assertThat(document.getText()).contains("차량번호");
    assertThat(document.getLines()).anyMatch(line ->
        line.contains("8713"));
  }

  /**
   * OCR 문서 내용 출력 (디버깅용)
   */
  private void printDocument(OcrDocument document, String name) {
    System.out.println("\n[" + name + "]");
    System.out.println("Confidence: " + document.getConfidence());
    System.out.println("\n--- Lines ---");
    for (int i = 0; i < document.getLines().size(); i++) {
      System.out.println(String.format("[%2d] %s", i,
          document.getLines().get(i)));
    }
    System.out.println("\n--- Full Text ---");
    System.out.println(document.getText());
  }


}
