# OCR Text Parser

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-9.3.0-blue.svg)](https://gradle.org/)

Naver OCR API 결과를 파싱하여 **계근지(weighing slip)** 데이터를 자동으로 추출하는 Spring Boot 기반 파서입니다.

## 📋 프로젝트 소개

계근지(계량증명서)는 물품의 무게를 측정한 증명서로, OCR로 스캔 시 다양한 포맷과 오류가 발생합니다. 이 프로젝트는 OCR 결과에서 필수 정보를 자동으로 추출하고 검증하는
시스템을 제공합니다.

### 주요 특징

- **자동 필드 추출**: 날짜, 차량번호, 무게(총중량/차중량/실중량), 거래처 정보 추출
- **하이브리드 추출 전략**: 텍스트 기반 + 좌표 기반 전략을 결합하여 추출 정확도 향상
- **Fuzzy Matching**: OCR 오류(오타, 띄어쓰기)를 허용하는 유사도 기반 매칭
- **자동 정규화**: 무게 값 검증, 소수점 처리, 단위 표준화
- **비즈니스 룰 검증**: 무게 계산 검증 (실중량 = 총중량 - 차중량)
- **확장 가능한 아키텍처**: SOLID 원칙을 따르는 모듈화된 설계

## 빠른 시작

### 실행 환경

- Java 17 이상
- Gradle 9.3.0 이상

### 설치 및 실행

```bash
# 저장소 클론
git clone https://github.com/yourusername/ocr-text-parser.git
cd ocr-text-parser

# 빌드
./gradlew build

# 실행 (샘플 파일 자동 파싱 데모)
./gradlew bootRun
```

### IDE에서 실행

`src/main/java/me/eunseong/ocrtextparser/demo/RunParsingDemo.java` 파일의 `main` 메서드를 실행하면 4개의 샘플 파일이
자동으로 파싱됩니다.

## 📊 실행 결과 예시

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
파일: samples/sample_01.json
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ 파싱 성공

📋 계근지 정보:
  - 날짜: 2026-02-02
  - 차량번호: 8713
  - 거래처: 곰욕환경폐기물
  - 품명: (없음)

⚖️  무게 정보:
  - 총중량: 12480.00 kg (측정: 2026-02-02T05:26:18)
  - 차중량: 7470.00 kg
  - 실중량: 5010.00 kg

🔍 무게 계산 검증:
  - 계산값: 5010.00 kg (총중량 - 차중량)
  - 실제값: 5010.00 kg
  - 차이: 0.00 kg
  - 검증: ✅ 통과
```

## 🏗️ 아키텍처

### 파싱 파이프라인

```
OcrDocument (Naver OCR API 결과)
    ↓
[1] Extraction (필드 추출)
    ├─ DateExtractor
    ├─ VehicleNumberExtractor
    ├─ WeightExtractor (총중량/차중량/실중량)
    └─ CompanyExtractor
    ↓
[2] Normalization (정규화)
    ├─ DateNormalizer
    └─ WeightNormalizer (음수 방지, 반올림, 단위 표준화)
    ↓
[3] Validation (검증)
    ├─ RequiredFieldValidator (필수 필드 확인)
    └─ BusinessRuleValidator (무게 계산 검증)
    ↓
ParsingResult (성공/실패 + 데이터 + 에러/경고)
```

### 적용된 디자인 패턴

- **Strategy Pattern**: 추출 전략 (`TextBasedStrategy`, `PositionBasedStrategy`)
- **Template Method Pattern**: 필드 추출기 공통 로직 (`AbstractFieldExtractor`)
- **Chain of Responsibility**: 파싱 파이프라인 (`DefaultParsingPipeline`)
- **Factory Pattern**: 결과 생성 (`ParsingResult.success()`, `ParsingResult.failure()`)
- **Registry Pattern**: 추출기 관리 (`ExtractorRegistry`)

## 📂 프로젝트 구조

```
src/main/java/me/eunseong/ocrtextparser/
├── config/                      # 설정
│   └── ParserProperties.java   # 파서 설정 (Fuzzy Matching 임계값 등)
├── domain/                      # 도메인 모델
│   ├── OcrDocument.java         # OCR 문서
│   ├── OcrWord.java             # OCR 단어 (좌표 포함)
│   ├── WeighingSlip.java        # 계근지 데이터
│   ├── Weight.java              # 무게 정보
│   └── ParsingResult.java       # 파싱 결과
├── extractor/                   # 필드 추출기
│   ├── strategy/                # 추출 전략
│   │   ├── ExtractionStrategy.java
│   │   ├── TextBasedStrategy.java       # 텍스트 기반 추출
│   │   └── PositionBasedStrategy.java   # 좌표 기반 추출
│   ├── weight/                  # 무게 추출기
│   │   ├── AbstractWeightFieldExtractor.java
│   │   ├── TotalWeightExtractor.java
│   │   ├── VehicleWeightExtractor.java
│   │   └── NetWeightExtractor.java
│   ├── AbstractFieldExtractor.java
│   ├── DateExtractor.java
│   ├── VehicleNumberExtractor.java
│   ├── CompanyExtractor.java
│   ├── WeightExtractor.java
│   └── ExtractorRegistry.java   # 추출기 레지스트리
├── normalizer/                  # 정규화
│   ├── Normalizer.java
│   ├── DateNormalizer.java
│   └── WeightNormalizer.java
├── validator/                   # 검증
│   ├── Validator.java
│   ├── RequiredFieldValidator.java
│   └── BusinessRuleValidator.java
├── pipeline/                    # 파싱 파이프라인
│   ├── ParsingPipeline.java
│   ├── DefaultParsingPipeline.java
│   └── ParsingContext.java      # 파싱 컨텍스트 (상태 공유)
├── service/                     # 서비스
│   └── ParsingService.java      # 메인 파싱 서비스
├── util/                        # 유틸리티
│   ├── TextNormalizer.java      # 텍스트 정규화
│   ├── TextMatcher.java         # Fuzzy Matching
│   ├── PositionHelper.java      # 좌표 계산
│   └── OcrDocumentLoader.java   # OCR 문서 로더
└── demo/                        # 데모
    ├── SampleParsingDemo.java   # 샘플 파싱 데모
    └── RunParsingDemo.java      # 실행 가능한 데모 클래스
```

## 🔧 주요 기능 상세

### 1. Fuzzy Matching

OCR 오류를 허용하는 유사도 기반 매칭 (Levenshtein Distance 알고리즘 사용)

```java
// "차랑번호" (오타) → "차량번호" 매칭 (유사도 0.75)
// "계 량 일 자" (띄어쓰기) → "계량일자" 매칭
```

**설정**: `application.properties`

```properties
app.parser.fuzzy-match-threshold=0.75
```

### 2. 하이브리드 추출 전략

**TextBasedStrategy** (우선순위 1)

- 키워드 기반 텍스트 매칭
- 라벨-값이 같은 줄 또는 다음 줄에 있는 경우 처리
- 시간 패턴 자동 제거

**PositionBasedStrategy** (우선순위 2)

- 좌표 기반 라벨-값 매칭
- 라벨 오른쪽의 같은 줄에서 값 추출
- TextBasedStrategy 실패 시 폴백

### 3. 무게 계산 검증

```java
// 비즈니스 룰 검증
실중량 =총중량 -

차중량(허용 오차: ±10.0kg)
```

## 🧪 테스트

### 전체 테스트 실행

```bash
./gradlew test
```

### 테스트 커버리지

- **단위 테스트**: 각 컴포넌트별 단위 테스트
- **통합 테스트**: 전체 파이프라인 end-to-end 테스트
- **샘플 테스트**: 4개의 실제 샘플 파일 파싱 테스트

```
✅ 48+ tests passing
- Extractor Tests (DateExtractor, VehicleNumberExtractor, WeightExtractor, etc.)
- Normalizer Tests (WeightNormalizer, DateNormalizer)
- Validator Tests (RequiredFieldValidator, BusinessRuleValidator)
- Integration Tests (ParsingServiceIntegrationTest)
```

## ⚙️ 설정

`src/main/resources/application.properties`

```properties
# Fuzzy Matching 임계값 (0.0 ~ 1.0, 높을수록 엄격)
app.parser.fuzzy-match-threshold=0.75
# 좌표 기반 추출 설정
app.parser.position.y-tolerance=80          # Y축 허용 오차 (px)
app.parser.position.x-min-offset=50         # X축 최소 간격 (px)
# 무게 계산 허용 오차 (kg)
app.parser.validation.weight-tolerance=10.0
```

## 🎯 사용 사례

### 프로그래밍 방식 사용

```java

@Autowired
private ParsingService parsingService;

@Autowired
private OcrDocumentLoader documentLoader;

public void parseDocument() {
  // 1. OCR 문서 로드
  OcrDocument document = documentLoader.loadFromResource("samples/sample_01.json");

  // 2. 파싱 실행
  ParsingResult result = parsingService.parse(document);

  // 3. 결과 처리
  if (result.isSuccess()) {
    WeighingSlip slip = result.getData();
    System.out.println("날짜: " + slip.getDate());
    System.out.println("차량번호: " + slip.getVehicleNumber());
    System.out.println("총중량: " + slip.getTotalWeight().getValue() + " kg");
  } else {
    result.getErrors().forEach(System.err::println);
  }
}
```

## 📈 향후 개선 계획

- [ ] REST API 엔드포인트 추가
- [ ] 다양한 계근지 포맷 지원 확장
- [ ] 머신러닝 기반 추출 정확도 개선
- [ ] 배치 파싱 기능
- [ ] 파싱 결과 엑셀 내보내기
- [ ] Web UI 추가

## 📝 라이선스

MIT License

## 👥 기여

이슈 및 PR은 언제나 환영합니다!

## 📧 문의

- 이슈 트래커: [GitHub Issues](https://github.com/yourusername/ocr-text-parser/issues)
