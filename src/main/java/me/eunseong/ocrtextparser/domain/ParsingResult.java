package me.eunseong.ocrtextparser.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsingResult {

  private boolean success;
  private WeighingSlip data;
  private List<String> errors;
  private List<String> warnings;
  private Double confidence;

}
