package me.eunseong.ocrtextparser.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OcrWord {

  private String text;
  private int x;  // boundingBox 좌상단 x
  private int y;  // boundingBox 좌상단 y
  private int width;  // 너비
  private int height;  // 높이
  private Double confidence;
}
