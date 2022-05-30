# 지하철 경로조회 피드백

## 1단계 피드백

- [x] `Fare` 객체의 용도
    - 현재 객체가 아닌 함수처럼 사용되는 것으로 보임
    - 거리를 전달하여 실제 계산은 객체가 수행하도록 수정
    - `new Fare(distance).longValue();`
- [x] `FareTest`
    - 경계값에 대한 테스트
- [x] `JGraphPathFinder`
    - 정적팩터리 메서드의 반환 타입을 인터페이스로 수정
- [ ] `Sections`
    - 구간 추가, 삭제 시 도메인 규칙을 따르지만 객체 생성 시에는 그렇지 않음.
    - [x] 객체 생성에 대한 검증의 필요성
- [x] `PathService`
    - `getPath()`
        - 구현체가 변경된다면 내부 구현을 수정해야함.
        - 코드의 변경없이 확장 가능하도록 구조 변경
    - `FactoryBean`을 사용하여 빈으로 주입받아 사용할 수 있도록 수정
- [x] `PathServiceTest`
    - 경로 조회 테스트 시 다양한 케이스에 대해 테스트
- [x] `AcceptanceTest`
    - 사용자 유스케이스에 대한 테스트 작성
        - 브라우저로 직접 테스트할 때 흐름을 AcceptanceTest에서 작성

## 2단계 피드백

- [x] 미사용 클래스 제거
  - `JGraphPathFinder` 클래스 미사용으로 제거 
- [x] `PathFinderFactory`
    - [x] `getObjectType()` 메서드 반환 타입 수정
      - 사용하는 클래스 수정한 것과 동일하게 `ShortestPathFinder.class`로 수정 
    - [x] 적절한 패키지 위치로 수정
      - `domain > pathfinder` 패키지로 수정  
- [x] `Fare`
    - [x] 상수를 사용하는 클래스에서 가지고 있도록 수정
    - [x] 인터페이스로 수정 후 구현하도록 수정
- [x] `DiscountPolicyFactory` / `FareFactory`
    - [x] 현재 구조 상으로 조건이 추가될 경우 분기문이 추가됨. 개선 필요
        - hint : 각 할인 정책이 할인 조건을 알고 있다면?
- [x] `Fare` / `DiscountPolicy`
    - 각각의 구현 클래스별로 테스트 코드를 별도로 가지고 있도록 수정
- [x] `AcceptanceTest`
    - `dao`가 아닌 `RestAssured`를 이용한 테스트  

