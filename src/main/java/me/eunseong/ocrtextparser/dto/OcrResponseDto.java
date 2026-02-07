package me.eunseong.ocrtextparser.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OcrResponseDto {

  private List<Page> pages;

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Page {

    private String text;
    private List<Line> lines;
    private List<Word> words;
    private Double confidence;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Word {

    private String text;
    private Double confidence;
    private BoundingBox boundingBox;
  }


  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Line {

    private String text;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class BoundingBox {

    private List<Vertex> vertices;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Vertex {

    private int x;
    private int y;
  }

}
