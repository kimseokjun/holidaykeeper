# Holiday Keeper

전 세계 공휴일 관리 시스템

## 기술 스택

- **Java**: 21
- **Spring Boot**: 3.4.0
- **Database**: H2 (In-Memory)
- **ORM**: Spring Data JPA, QueryDSL
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Gradle

## 빌드 & 실행 방법

### 1. 프로젝트 클론

```bash
git clone https://github.com/kimseokjun/holidaykeeper.git
cd holiday-keeper
```

### 2. 빌드

```bash
./gradlew clean build
```

### 3. 실행

```bash
./gradlew bootRun
```

### 4. 접속

- **애플리케이션**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
    - JDBC URL: `jdbc:h2:mem:holidaydb`
    - Username: `sa`
    - Password: (공백)

## 테스트 실행

```bash
./gradlew clean test
```

## REST API 명세

### 1. 공휴일 검색

**Endpoint**: `POST /api/holidays/search`

**Request Body**:

```json
{
  "year": 2024,
  "countryCode": "KR",
  "from": "2024-01-01",
  "to": "2024-12-31",
  "type": "Public",
  "fixed": false,
  "global": true
}
```

**Query Parameters**:

- `page` (optional, default: 0): 페이지 번호
- `size` (optional, default: 20): 페이지 크기

**Response**:

```json
{
  "content": [
    {
      "id": 1,
      "countryCode": "KR",
      "countryName": "South Korea",
      "date": "2024-01-01",
      "localName": "신정",
      "name": "New Year's Day",
      "fixed": false,
      "global": true,
      "counties": null,
      "launchYear": null,
      "types": "Public"
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 15,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

---

### 2. 공휴일 재동기화 (Upsert)

**Endpoint**: `POST /api/holidays/refresh`

**Query Parameters**:

- `year` (required): 연도
- `countryCode` (required): 국가 코드

**Example**:

```
POST /api/holidays/refresh?year=2024&countryCode=KR
```

**Response**:

```
덮어쓰기 성공
```

**동작**:

- 외부 API에서 최신 데이터 조회
- 기존 데이터와 비교하여 Upsert (INSERT/UPDATE/DELETE)

---

### 3. 공휴일 삭제

**Endpoint**: `DELETE /api/holidays/delete`

**Query Parameters**:

- `year` (required): 연도
- `countryCode` (required): 국가 코드

**Example**:

```
DELETE /api/holidays/delete?year=2024&countryCode=KR
```

**Response**:

```
삭제 성공
```

**동작**:

- 특정 연도와 국가의 모든 공휴일 데이터 삭제

---

## 주요 기능

### 1. 초기 데이터 적재

- Nager.Date API로부터 250개 국가의 2020~2025년 공휴일 데이터 자동 적재
- 국가별/연도별 트랜잭션 분리로 부분 실패 허용
- 애플리케이션 시작 시 자동 실행

### 2. 검색 및 페이징

- QueryDSL 동적 쿼리로 다중 필터 지원
- 연도, 국가, 기간(from~to), 타입, 고정 여부, 전국 여부 필터링
- 페이징 처리 (기본 20개)

### 3. 재동기화 (Upsert)

- 외부 API 최신 데이터와 DB 비교
- 같은 날짜+이름 있으면 UPDATE, 없으면 INSERT
- API에 없는 데이터는 DELETE

### 4. 삭제

- 특정 연도와 국가의 공휴일 데이터 일괄 삭제

### 5. 배치 자동화

- 매년 1월 2일 01:00 KST에 전년도·금년도 데이터 자동 동기화
- Spring `@Scheduled` 사용

## 데이터베이스 스키마

### Countries 테이블

```sql
CREATE TABLE countries
(
    country_code VARCHAR(2) PRIMARY KEY,
    country_name VARCHAR(255) NOT NULL
);
```

### Holidays 테이블

```sql
CREATE TABLE holidays
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    country_code VARCHAR(2)   NOT NULL,
    date         DATE         NOT NULL,
    local_name   VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    fixed        BOOLEAN,
    global       BOOLEAN,
    counties     VARCHAR(255),
    launch_year  INTEGER,
    types        VARCHAR(255),
    FOREIGN KEY (country_code) REFERENCES countries (country_code),
    CONSTRAINT uk_country_date_name UNIQUE (country_code, date, name)
);

CREATE INDEX idx_country_date ON holidays (country_code, date);
CREATE INDEX idx_date ON holidays (date);
```

## API 문서 확인 방법

### Swagger UI

1. 애플리케이션 실행
2. 브라우저에서 접속: http://localhost:8080/swagger-ui.html
3. API 목록 확인 및 테스트 가능

### OpenAPI JSON

- http://localhost:8080/api-docs

## 프로젝트 구조

```
src/main/java/com/planitsquare/holidaykeeper/
├── HolidayKeeperApplication.java # 메인 애플리케이션
├── config/
│   ├── DataLoader.java           # 초기 데이터 적재
│   ├── QuerydslConfig.java       # QueryDSL 설정
│   ├── RestClientConfig.java     # RestClient 설정
│   └── SwaggerConfig.java        # Swagger 설정
├── controller/
│   └── HolidayController.java    # REST API 컨트롤러
├── service/
│   └── HolidayService.java       # 비즈니스 로직
├── repository/
│   ├── CountryRepository.java
│   ├── HolidayRepository.java
│   ├── HolidayRepositoryCustom.java
│   └── HolidayRepositoryImpl.java # QueryDSL 구현
├── entity/
│   ├── Country.java              # 국가 엔티티
│   └── Holiday.java              # 공휴일 엔티티
├── dto/
│   ├── request/
│   │   └── HolidaySearchRequest.java # 검색 요청 DTO
│   └── response/
│       ├── CountryResponse.java      # 국가 응답 DTO (외부 API)
│       ├── HolidayResponse.java      # 공휴일 응답 DTO (외부 API)
│       ├── HolidayDto.java           # 공휴일 응답 DTO
│       └── PageResponse.java         # 페이징 응답 DTO
├── client/
│   └── NagerDateClient.java      # 외부 API 호출
└── scheduler/
    └── HolidayScheduler.java     # 배치 스케줄러
```

## 테스트

- Repository 테스트: QueryDSL 동적 쿼리 검증
- Service 테스트: 비즈니스 로직 검증
- Controller 테스트: API 엔드포인트 검증

### ./gradlew clean test 성공 스크린샷

<img width="1535" height="245" alt="image" src="https://github.com/user-attachments/assets/e299c002-9677-477c-90df-36d542af1ed1" />

