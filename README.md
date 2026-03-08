# GiftLedger

축의금 관리가 생각보다 복잡하다. 누구한테 얼마를 줬는지, 나중에 나 결혼할 때 얼마 돌려받았는지, 아직 못 받은 사람은 누군지... 결혼식 끝나고 카카오뱅크 내역 뒤지는 게 일이었다면 이 서비스가 그 불편함을 해결해준다.

경조사별로 지인의 축의금을 기록하고, 내가 준 돈 대비 얼마나 회수됐는지 분석해주는 Spring Boot 기반 웹 애플리케이션이다.

---

## 어떤 기능이 있냐면

- **이벤트 관리** — 결혼식, 장례식, 생일 등 경조사를 등록하고, 참석한 지인과 축의금 내역을 함께 기록
- **지인 관리** — 이름, 관계(가족/친구/직장동료), 그룹, 연락처를 저장하고 검색
- **분석 대시보드** — 총 지출/수입, 잔액, 회수율을 한 눈에
- **지출 패턴** — 월별/요일별/이벤트 유형별로 내 지출이 어떻게 분포되는지
- **회수율 분석** — 아직 못 받은 지인 목록과 미회수 금액
- **연령대별 통계** — 비로그인 상태에서도 "30대 결혼식 평균 축의금이 얼마?" 같은 통계를 볼 수 있음

---

## 기술 스택

| 구분 | 내용 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.8 |
| ORM | Spring Data JPA (Hibernate) |
| DB | MySQL 8 |
| 인증 | JWT (jjwt 0.12.3) |
| 보안 | Spring Security |
| API 문서 | Springdoc OpenAPI (Swagger UI) |
| 테스트 | JUnit 5, Spring Security Test |
| 커버리지 | JaCoCo |
| 빌드 | Gradle |

---

## 프로젝트 구조

```
src/main/java/springboot/giftledger/
├── auth/           # 로그인, 회원가입, 연령대별 통계
├── event/          # 이벤트 CRUD, 축의금 내역 관리
├── acquaintance/   # 지인 목록 조회
├── analysis/       # 대시보드, 패턴, 지인 분석, 회수율
├── entity/         # Member, Event, Acquaintance, EventAcquaintance, GiftLog
├── repository/     # JPA Repository
├── security/       # JWT 필터, UserDetails
├── enums/          # EventType, Relation, ActionType, PayMethod
└── common/         # 예외처리, 공통 응답 DTO
```

### 도메인 관계

```
Member
  ├── Event (1:N)
  │     └── EventAcquaintance (N:M 매핑)
  │           └── GiftLog (축의금 기록)
  └── Acquaintance (1:N)
        └── EventAcquaintance (N:M 매핑)
```

한 이벤트에 여러 지인이 참석하고, 한 지인이 여러 이벤트에 참석할 수 있어서 Event-Acquaintance는 다대다 관계다. `EventAcquaintance`가 그 중간 테이블 역할을 하고, 실제 축의금 금액은 `GiftLog`에 남긴다.

---

## API 목록

### 인증 불필요
| Method | URL | 설명 |
|--------|-----|------|
| POST | `/auth/login` | 로그인 (JWT 발급) |
| POST | `/users` | 회원가입 |
| GET | `/statistics/event` | 연령대별 평균 축의금 조회 |

### 인증 필요 (Header: `X-AUTH-TOKEN`)
| Method | URL | 설명 |
|--------|-----|------|
| POST | `/events` | 이벤트 등록 |
| GET | `/events` | 이벤트 목록 (페이징) |
| GET | `/events/{eventId}` | 이벤트 상세 |
| PUT | `/events/{eventId}` | 이벤트 수정 |
| DELETE | `/events/{giftId}` | 축의금 기록 삭제 |
| GET | `/events/details/{giftId}` | 축의금 상세 조회 |
| POST | `/events/{eventId}` | 기존 이벤트에 축의금 추가 |
| GET | `/acquaintance` | 지인 목록 |
| GET | `/analysis/dashboard` | 대시보드 통계 |
| GET | `/analysis/recent-events` | 최근 이벤트 5개 |
| GET | `/analysis/pattern` | 월별/요일별 지출 패턴 |
| GET | `/analysis/relation` | TOP 5 지인 및 회수율 |
| GET | `/analysis/recovery` | 미회수 내역 |

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## 실행 방법

### 사전 준비
- Java 17
- MySQL 8 (로컬에 `giftledger` 데이터베이스 생성 필요)

### DB 설정

```sql
CREATE DATABASE giftledger;
```

`application.yml`의 DB 접속 정보를 환경에 맞게 수정:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/giftledger
    username: root
    password: 1234
```

테이블은 JPA `ddl-auto: update` 설정으로 애플리케이션 실행 시 자동 생성된다.

### 빌드 및 실행

```bash
./gradlew bootRun
```

또는

```bash
./gradlew build
java -jar build/libs/GiftLedger-*.jar
```

서버가 뜨면 `http://localhost:8080`에서 프론트엔드 페이지에 접근할 수 있다.

---

## 프론트엔드 페이지

| 파일 | 설명 |
|------|------|
| `/index.html` | 메인 (연령대별 통계) |
| `/login.html` | 로그인 |
| `/register.html` | 회원가입 |
| `/eventregister.html` | 이벤트 등록 |
| `/event.html` | 이벤트 목록 |
| `/detailevent.html` | 이벤트 상세 |
| `/eventupdate.html` | 이벤트 수정 |
| `/analysis.html` | 분석 대시보드 |

JWT 토큰은 `sessionStorage`에 저장하고, 인증이 필요한 모든 API 요청에 `X-AUTH-TOKEN` 헤더로 전달한다.

---

## 테스트

```bash
./gradlew test
```

커버리지 리포트는 `build/reports/jacoco/test/html/index.html`에서 확인.

테스트는 Controller/Service 레이어별로 작성되어 있고, config, entity, security, enum 같은 설정성 클래스는 커버리지 측정에서 제외했다.

---

## 열거형 타입

| Enum | 값 |
|------|----|
| `EventType` | WEDDING, FUNERAL, BIRTHDAY, ETC |
| `Relation` | FAMILY, FRIEND, COWORKER, ETC |
| `ActionType` | GIVE(출금), TAKE(입금) |
| `PayMethod` | CASH, BANK_TRANSFER, KAKAOPAY |

---

## 참고

- JWT 토큰 유효기간: 24시간
- 이벤트 목록 기본 정렬: 날짜 내림차순, 페이지당 5개
- 지인 검색: 이름 키워드 검색 지원
- 패턴 분석의 기본 연도: 현재 연도 (파라미터로 변경 가능)
