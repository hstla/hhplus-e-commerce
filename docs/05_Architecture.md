# 아키텍처 설계
## 목차
- [요구사항 분석](01_Requirements_Analysis.md)
- [ERD](03_erd.md)
- [핵심 로직 다이어그램](02_Sequence_Diagram.md)
- [플로우 차트](04_flowchart.md)
- [아키텍처 및 패키지 구조](05_Architecture.md)
- [↩️ README로 돌아가기](../README.md#주요기능-및-아키텍처-링크들)

## 1. 아키텍처 개요
본 프로젝트는 **클린 아키텍처(Clean Architecture)** 를 기반으로 설계되었습니다.
초기에는 헥사고날 아키텍처(Hexagonal Architecture)를 목표로 하였으나,
보다 실용적이고 직관적인 구현을 위해 클린 아키텍처의 핵심 원칙을 적용하여 타협점을 찾았습니다.

이를 통해 핵심 비즈니스 로직(Domain)을 외부 기술(Infrastructure)로부터 분리하고, 시스템의 유연성,
테스트 용이성, 유지보수성을 극대화하는 것을 목표로 합니다.

## **2. 주요 설계 의도**
* **관심사 분리 (Separation of Concerns)**
    * **도메인(Domain)**, **애플리케이션(Application)**, **인프라스트럭처(Infrastructure)** 세 가지 핵심 계층으로 역할을 명확히 분리하여 코드의 복잡도를 낮춥니다.
* **의존성 규칙 (The Dependency Rule)**
    * 모든 의존성이 외부(Infrastructure)에서 내부(Domain)로 향하도록 설계하여, 핵심 비즈니스 로직이 외부 기술 변화에 영향을 받지 않도록 보호합니다.
    * Domain ← Application ← Infrastructure
* **인터페이스를 통한 의존성 역전 (Dependency Inversion)**
    * 내부 계층(Domain)에서 리포지토리 등의 인터페이스를 정의하고, 외부 계층(Infrastructure)에서 이를 구현하도록 하여 의존성 규칙을 강제합니다.
* **테스트 용이성 확보**
    * 핵심 도메인 로직은 프레임워크나 데이터베이스에 대한 의존성이 없으므로, 빠르고 독립적인 단위 테스트가 가능합니다.

## 3. 패키지 구조
```
kr.hhplus.be
├── application                         # 애플리케이션 레이어: 유스케이스, DTO, 리스너 정의
│   ├── coupon/usecase                  # 쿠폰 발급/사용 유스케이스
│   ├── order/usecase                   # 주문 생성/조회 유스케이스
│   ├── payment/usecase                 # 결제 처리 유스케이스
│   ├── product/usecase                 # 상품 조회/검색 유스케이스
│   ├── user/usecase                    # 사용자 잔액 충전/조회 유스케이스
│   └── usercoupon/usecase              # 사용자 쿠폰 관리 유스케이스
├── domain                              # 도메인 레이어: 핵심 비즈니스 로직 및 모델
│   ├── common/event                    # 공통 도메인 이벤트
│   ├── coupon                          # 쿠폰 도메인 (모델, 리포지토리 인터페이스, 도메인 서비스)
│   └── ....                            # 다른 도메인 로직들
├── global                              # 전역 설정 및 공통 모듈
│   ├── common                          # 공통 코드 (AOP, Kafka/Redis 설정 등)
│   ├── config                          # 주요 설정 (JPA, Cache, Async 등)
│   └── error                           # 전역 예외 처리 핸들러 및 ErrorCode
└── infrastructure                      # 인프라스트럭처 레이어: 외부 시스템 연동, 영속성 구현
    ├── kafka                           # Kafka 연동 (Producer/Consumer)
    ├── persistence                     # RDBMS 영속성 구현 (JPA 엔티티, 리포지토리 구현체)
    │   ├── coupon
    │   └── ....
    ├── redis                           # Redis 연동 (캐시, 분산락 등)
    ├── sse                             # SSE (Server-Sent Events) 서비스
    └── web/controller                  # 프레젠테이션 레이어 (API 컨트롤러)
```

## **4. 계층별 책임 및 역할**

| 계층 | 패키지 경로 | 주요 책임 및 역할|
| :---- | :---- |:----------------------------------------------------------------------------------------------------------------------------------------------|
| **프레젠테이션 (Presentation)** | infrastructure/web/controller | - 외부 HTTP 요청을 수신하고 응답하는 API 엔드포인트 <br/>- 요청 데이터를 DTO 형태로 변환하여 application 계층에 전달|
| **애플리케이션 (Application)** | application/{domain}/usecase | - 시스템의 사용 사례(Use Case)를 구체적으로 정의 <br/>- domain 계층의 엔티티와 서비스를 조율하여 비즈니스 흐름을 처리 <br/>- 트랜잭션 경계 설정|
| **도메인 (Domain)** | domain | - 시스템의 가장 핵심적인 비즈니스 규칙과 데이터를 포함 <br/>- 외부 계층에 의존하지 않는 순수한 도메인 모델(Entity, VO) <br/>- 영속성을 위한 리포지토리 인터페이스 정의|
| **인프라스트럭처 (Infrastructure)** | infrastructure | - 데이터베이스, 메시징 큐, 캐시 등 외부 기술과의 연동을 구현 <br/>- domain 계층에서 정의한 리포지토리 인터페이스를 JPA 등으로 구현 <br/>- 외부 시스템과 통신하는 어댑터(Adapter) 역할|
| **글로벌 (Global)** | global | - 여러 계층에서 공통으로 사용되는 전역 설정, 예외 처리, 유틸리티 등 <br/>- 횡단 관심사(Cross-cutting Concerns) 처리|