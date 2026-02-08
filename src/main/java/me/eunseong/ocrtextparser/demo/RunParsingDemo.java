package me.eunseong.ocrtextparser.demo;

import me.eunseong.ocrtextparser.OcrTextParserApplication;
import org.springframework.boot.SpringApplication;

/**
 * 파싱 데모 실행 클래스
 * IDE에서 실행 버튼으로 직접 실행 가능
 */
public class RunParsingDemo {

  public static void main(String[] args) {
    System.out.println("\n" + "=".repeat(80));
    System.out.println("🚀 OCR Text Parser Demo 시작");
    System.out.println("=".repeat(80) + "\n");

    // Spring Boot 애플리케이션 실행
    // SampleParsingDemo가 CommandLineRunner로 등록되어 있어 자동 실행됨
    SpringApplication.run(OcrTextParserApplication.class, args);
  }
}
