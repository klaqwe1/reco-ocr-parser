package me.eunseong.ocrtextparser.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.eunseong.ocrtextparser.dto.OcrResponseDto;
import me.eunseong.ocrtextparser.domain.OcrDocument;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

@Component
public class OcrDocumentLoader {

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * 파일에서 OCR 문서 로드
   *
   * @param filePath
   * @return
   * @throws IOException
   */
  public OcrDocument loadFromFile(String filePath) throws IOException {
    File file = new File(filePath);
    OcrResponseDto response = objectMapper.readValue(file, OcrResponseDto.class);
    return convertToOcrDocument(response);
  }

  /**
   * 리소스에서 OCR 문서 로드 / 추후 텍스트 입력으로 변경 가능
   *
   * @param resourcePath
   * @return
   * @throws IOException
   */
  public OcrDocument loadFromResource(String resourcePath) throws
      IOException {
    InputStream inputStream = getClass().getClassLoader()
        .getResourceAsStream(resourcePath);

    if (inputStream == null) {
      throw new IOException("Resource not found: " + resourcePath);
    }

    OcrResponseDto response = objectMapper.readValue(inputStream,
        OcrResponseDto.class);
    return convertToOcrDocument(response);
  }

  /**
   * 응답을 도메인 모델로 변환
   *
   * @param response
   * @return
   */
  private OcrDocument convertToOcrDocument(OcrResponseDto response) {
    if (response.getPages() == null || response.getPages().isEmpty()) {
      throw new IllegalArgumentException("No pages in OCR response");
    }

    OcrResponseDto.Page firstPage = response.getPages().get(0); // 현재 문서는 단일 page

    return OcrDocument.builder()
        .text(firstPage.getText())
        .lines(firstPage.getLines().stream()
            .map(OcrResponseDto.Line::getText)
            .collect(Collectors.toList()))
        .confidence(firstPage.getConfidence())
        .build();
  }

}
